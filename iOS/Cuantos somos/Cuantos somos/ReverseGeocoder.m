#import "ReverseGeocoder.h" 

#define kTimeoutInterval 20

static NSString *const urlReverseGeocoder = @"http://nominatim.openstreetmap.org/reverse?format=json&accept-language=en&zoom=18&addressdetails=1&lat=%f&lon=%f";

@implementation ReverseGeocoder

@synthesize responseData, connection, request, state, timeoutTimer, completionBlock;

+ (ReverseGeocoder *)reverseGeocode:(CLLocationCoordinate2D)coordinate 
                         completion:(void (^)(NSMutableDictionary *, NSError *))block {
    ReverseGeocoder *geocoder = [[self alloc] initWithParameters:coordinate completion:block];
    [geocoder start];
    return geocoder;
}

- (ReverseGeocoder *)initWithParameters:(CLLocationCoordinate2D)coordinate 
                            completion:(void (^)(NSMutableDictionary *, NSError *))block {
    self = [super init];
    self.completionBlock = block;
    
    NSString *url = [NSString stringWithFormat:urlReverseGeocoder, coordinate.latitude, coordinate.longitude];
    
    self.request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
    [self.request setTimeoutInterval:kTimeoutInterval];
    
    self.state = RequestStateReady;
    
    return self;
}

- (void)setTimeoutTimer:(NSTimer *)newTimer {
    
    if(timeoutTimer){
        [timeoutTimer invalidate];
        timeoutTimer = nil;
    }
    
    if(newTimer){
        timeoutTimer = newTimer;
    }
}

- (void)start {
    if(self.isCancelled) {
        [self finish];
        return;
    }
    
    // NSOperationQueue calls start from a bg thread (through GCD), but NSURLConnection already does that by itself
    if(![NSThread isMainThread]) { 
        [self performSelectorOnMainThread:@selector(start) withObject:nil waitUntilDone:NO];
        return;
    }
    
    [self willChangeValueForKey:@"isExecuting"];
    self.state = RequestStateExecuting;    
    [self didChangeValueForKey:@"isExecuting"];
    
    self.responseData = [[NSMutableData alloc] init];
    self.timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:kTimeoutInterval target:self selector:@selector(requestTimeout) userInfo:nil repeats:NO];
    
    self.connection = [[NSURLConnection alloc] initWithRequest:self.request delegate:self startImmediately:YES];
}

- (void)finish {
    [connection cancel];
    connection = nil;
    
    [self willChangeValueForKey:@"isExecuting"];
    self.state = RequestStateFinished;    
    [self didChangeValueForKey:@"isExecuting"];
    
    self.timeoutTimer = nil;
}

- (void)cancel {
    if([self isFinished])
        return;
    
    [super cancel];
    [self finish];
}

- (BOOL)isConcurrent {
    return YES;
}

- (BOOL)isFinished {
    return self.state == RequestStateFinished;
}

- (BOOL)isExecuting {
    return self.state == RequestStateExecuting;
}

- (void)startAsynchronous {
	[self start];
}

- (void)requestTimeout {
    NSError *timeoutError = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorTimedOut userInfo:nil];
    [self connection:nil didFailWithError:timeoutError];
}

- (void)dealloc {
    [connection cancel];
    
    self.timeoutTimer = nil;
    self.completionBlock = nil;
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[self.responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {	
    self.completionBlock(nil, error);
    
    [self finish];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	NSError *error;
    NSMutableDictionary *posicionActual;
    
    if(responseData && responseData.length > 0) {
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:responseData options:kNilOptions error:&error];
        
        // Componemos el json según las necesidades de la API Rest
        posicionActual = [NSMutableDictionary dictionaryWithDictionary:[json valueForKey:@"address"]];
        [posicionActual setValue:[json valueForKey:@"lat"] forKey:@"latitud"];
        [posicionActual setValue:[json valueForKey:@"lon"] forKey:@"longitud"];
        [posicionActual setValue:[json valueForKey:@"osm_id"] forKey:@"osm_id"];
        [posicionActual setValue:[json valueForKey:@"place_id"] forKey:@"place_id"];
        
        // Las calles peatonales no vienen como 'road' sino como 'pedestrian' 
        if(![posicionActual valueForKey:@"road"] && [posicionActual valueForKey:@"pedestrian"]){
            [posicionActual setValue:[posicionActual valueForKey:@"pedestrian"] forKey:@"road"];
        }
    }
    
    if(!error) {
        self.completionBlock(posicionActual, error);
    }
    else{
        self.completionBlock(nil, error);
    }
    
    [self finish];
}
@end
