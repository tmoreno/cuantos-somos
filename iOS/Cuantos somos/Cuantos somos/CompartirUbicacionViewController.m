#import <QuartzCore/QuartzCore.h>
#import <Twitter/Twitter.h>
#import "TMBAppDelegate.h"
#import "CompartirUbicacionViewController.h"
#import "ReverseGeocoder.h"
#import "NSString+URLEncoding.h"
#import "UIImage+ImagenPersonas.h"
#import "UIImageView+Persona.h"
#import "UIDevice+IdentifierAddition.h"

#ifdef DEBUG
#define urlPosicion @"http://192.168.1.2:8080/cuantossomos-rest/insertarposicionusuario?p="
#define urlPersonas @"http://192.168.1.2:8080/cuantossomos-rest/getnumeropersonas?p="
#else
#define urlPosicion @"https://cuantossomos-rest.cloudfoundry.com/insertarposicionusuario?p="
#define urlPersonas @"https://cuantossomos-rest.cloudfoundry.com/getnumeropersonas?p="
#endif

static NSInteger const kDistanceFilter = 10;
static NSInteger const kTimeout = 5;
static NSInteger const kAltura4Pulgadas = 568;

@implementation CompartirUbicacionViewController

@synthesize locationManager = _locationManager;

@synthesize fondoImageView = _fondoImageView;
@synthesize posicionLabel = _posicionLabel;
@synthesize insertarPosicionUsuarioButton = _insertarPosicionUsuarioButton;
@synthesize getNumeroPersonasButton = _getNumeroPersonasButton;
@synthesize twitearButton = _twitearButton;
@synthesize activityIndicatorView = _activityIndicatorView;

- (id)initWithCoder:(NSCoder*)aDecoder 
{
    if(self = [super initWithCoder:aDecoder]) {
        numberFormatter = [[NSNumberFormatter alloc] init];
        [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
        
        timestampFormatter = [[NSDateFormatter alloc] init];
        [timestampFormatter setDateFormat:@"dd/MM/yyyy HH:mm:ss"];
        
        dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"dd/MM/yyyy"];
        
        timeFormatter = [[NSDateFormatter alloc] init];
        [timeFormatter setDateFormat:@"HH:mm:ss"];
        
        _locationManager = [[CLLocationManager alloc] init];
        [_locationManager setDelegate:self];
        [_locationManager setDesiredAccuracy:kCLLocationAccuracyNearestTenMeters];
        [_locationManager setDistanceFilter:kDistanceFilter];
        [_locationManager startUpdatingLocation];
    }
    
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _posicionLabel.layer.borderColor = [UIColor grayColor].CGColor;
    _posicionLabel.layer.borderWidth = 3.0;
    [_posicionLabel setText:NSLocalizedString(@"obteniendoPosicion", nil)];
    
    [_insertarPosicionUsuarioButton setTitle:NSLocalizedString(@"cuantossomos", nil) forState:UIControlStateNormal];
    if([[UIScreen mainScreen] bounds].size.height == kAltura4Pulgadas){
        [_insertarPosicionUsuarioButton setFrame:CGRectMake(_insertarPosicionUsuarioButton.frame.origin.x,
                                                            kAltura4Pulgadas/2.7,
                                                            _insertarPosicionUsuarioButton.frame.size.width,
                                                            _insertarPosicionUsuarioButton.frame.size.height)];
        
        [_twitearButton setFrame:CGRectMake(_twitearButton.frame.origin.x,
                                            450,
                                            _twitearButton.frame.size.width,
                                            _twitearButton.frame.size.height)];
    }
    
    activityIndicatorContainerView = [[UIView alloc] initWithFrame:self.view.bounds];
    [_activityIndicatorView setFrame:CGRectMake(121, 200, 77, 77)];
    _activityIndicatorView.layer.cornerRadius = 10.0f;
    _activityIndicatorView.layer.shadowOffset = CGSizeZero;
    _activityIndicatorView.layer.shadowColor = [UIColor blackColor].CGColor;
    _activityIndicatorView.layer.shadowOpacity = 1;
    _activityIndicatorView.layer.shadowRadius = 110;
    _activityIndicatorView.layer.shadowPath = [UIBezierPath bezierPathWithRect:
                                              CGRectInset(_activityIndicatorView.bounds, -40, -40)].CGPath;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _fondoImageView = nil;
    _posicionLabel = nil;
    _insertarPosicionUsuarioButton = nil;
    _getNumeroPersonasButton = nil;
    _twitearButton = nil;
    _activityIndicatorView = nil;
}

/**
 *  Método que manda la petición de inserta la posición del usuario en el servidor
 */
- (IBAction) insertarPosicionUsuario:(id)sender
{
    [posicionACompartir setValue:@"IOS" forKey:@"plataforma"];
    [posicionACompartir setValue:[timestampFormatter stringFromDate:[[NSDate alloc] init]] forKey:@"fecha"];
    [posicionACompartir setValue:[[UIDevice currentDevice] uniqueGlobalDeviceIdentifier] forKey:@"idUsuario"];
    
    NSData *data = [NSJSONSerialization dataWithJSONObject:posicionACompartir 
                                                   options:kNilOptions 
                                                     error:nil];
    
    [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] urlEncode];
    
    NSString *url = [NSString stringWithFormat:@"%@%@", urlPosicion, [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] urlEncode]];    
    
    NSMutableURLRequest *peticion = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] 
                                                            cachePolicy:NSURLRequestUseProtocolCachePolicy 
                                                        timeoutInterval:kTimeout];
    [peticion setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [peticion setValue:@"application/json" forHTTPHeaderField:@"accept"];
    
    responseData = [NSMutableData data];
    posicionConnection = [[NSURLConnection alloc] initWithRequest:peticion delegate:self];
    
    [self.view addSubview:activityIndicatorContainerView];
    [_activityIndicatorView startAnimating];
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];   
}

/**
 *  Método que consulta el número de personas que hay en la posición en la que estas
 */
- (IBAction) getNumeroPersonas:(id)sender
{
    numeroPersonasParams = [[NSMutableDictionary alloc] init];
    [numeroPersonasParams setValue:idPosicion forKey:@"idPosicion"];
    [numeroPersonasParams setValue:[timestampFormatter stringFromDate:[[NSDate alloc] init]] forKey:@"fecha"];
    
    NSData *data = [NSJSONSerialization dataWithJSONObject:numeroPersonasParams 
                                                   options:kNilOptions 
                                                     error:nil];
    
    [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] urlEncode];
    
    
    NSString *url = [NSString stringWithFormat:@"%@%@", urlPersonas, [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] urlEncode]];
    
    NSMutableURLRequest *peticion = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] 
                                                            cachePolicy:NSURLRequestUseProtocolCachePolicy 
                                                        timeoutInterval:kTimeout];
    [peticion setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [peticion setValue:@"application/json" forHTTPHeaderField:@"accept"];
    
    responseData = [NSMutableData data];
    [NSURLConnection connectionWithRequest:peticion delegate:self];
    
    [self.view addSubview:activityIndicatorContainerView];
    [_activityIndicatorView startAnimating];
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
}

/**
 *  Método que permite twittear el número de personas que hay donde estás
 */
- (IBAction) twitearNumeroPersonas:(id)sender
{
    if([TWTweetComposeViewController canSendTweet]) {
        
        TWTweetComposeViewController *tweetVC = [[TWTweetComposeViewController alloc] init];
        
        NSString *textoTweet = [NSString stringWithFormat:NSLocalizedString(@"somosEn", nil), 
                                [numberFormatter stringFromNumber:numPersonas],
                                [[posicionACompartir valueForKey:@"road"] stringByReplacingOccurrencesOfString:@" " withString:@""]];
        
        [tweetVC setInitialText:textoTweet];
        
        [self presentViewController:tweetVC animated:YES completion:NULL];
    }
    else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"conexionErrorTitulo", nil)
                                                        message:NSLocalizedString(@"twitterError", nil)
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[responseData setLength:0];
    
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    httpStatusCode = [httpResponse statusCode];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    [activityIndicatorContainerView removeFromSuperview];
    [_activityIndicatorView stopAnimating];
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"conexionErrorTitulo", nil)
                                                    message:NSLocalizedString(@"conexionErrorDesc", nil) 
                                                   delegate:nil 
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
    
	NSLog(@"%@: %@", NSLocalizedString(@"conexionErrorTitulo", nil), [error description]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    [activityIndicatorContainerView removeFromSuperview];
    [_activityIndicatorView stopAnimating];
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    
    if(httpStatusCode == 200) {
        NSError *error;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:responseData options:kNilOptions error:&error];
        
        numPersonas = [json objectForKey:@"numPersonas"];
        
        if(connection == posicionConnection){
            idPosicion = [json objectForKey:@"idPosicion"];
            [self guardarPosicion:[posicionACompartir valueForKey:@"fecha"]];
        }
        else{
            [self guardarPosicion:[numeroPersonasParams valueForKey:@"fecha"]];
        }
        
        [_fondoImageView setImage:[UIImage initWithPersonas:[numPersonas integerValue]]];
        [_twitearButton setHidden:NO];
        
        [_getNumeroPersonasButton setTitle:[NSString stringWithFormat:NSLocalizedString(@"somos", nil), [numberFormatter stringFromNumber:numPersonas]] 
                                  forState:UIControlStateNormal];
        [_getNumeroPersonasButton setHidden:NO];
        [_insertarPosicionUsuarioButton setHidden:YES];
        
        [self moverPersona:numPersonas.intValue - numPersonasAnterior.intValue];
        
        numPersonasAnterior = numPersonas;
    }
    else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"conexionErrorTitulo", nil)
                                                        message:NSLocalizedString(@"conexionErrorDesc", nil) 
                                                       delegate:nil 
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

- (void) guardarPosicion:(NSString *)fecha
{
    NSError* error;
    
    TMBAppDelegate *delegado = [TMBAppDelegate sharedAppController];
    NSManagedObjectContext *moc = [delegado managedObjectContext];
    
    NSString *dia = [fecha substringToIndex:[fecha rangeOfString:@" "].location];
    NSString *hora = [fecha substringFromIndex:[fecha rangeOfString:@" "].location + 1];
    
    NSManagedObject *posicion = [delegado getPosicion:idPosicion byDate:[dateFormatter dateFromString:dia]];
    
    if (posicion == nil){
        posicion = [delegado getPosicion:idPosicion];
        
        if(posicion == nil){
            posicion = [NSEntityDescription insertNewObjectForEntityForName:@"Posicion" 
                                                     inManagedObjectContext:moc];
            [posicion setValue:idPosicion forKey:@"idPosicion"];
            [posicion setValue:[posicionACompartir valueForKey:@"road"] forKey:@"calle"];
            [posicion setValue:[NSString stringWithFormat:@"%@, %@", 
                                [posicionACompartir valueForKey:@"city"],
                                [posicionACompartir valueForKey:@"state"]] forKey:@"ciudad"];
            if(![moc save:&error]){
                NSLog(@"Error %@", [error localizedDescription]);
            }
        }
        
        posicion = [NSEntityDescription insertNewObjectForEntityForName:@"NumPersonasPosicion" 
                                                 inManagedObjectContext:moc];
        [posicion setValue:idPosicion forKey:@"idPosicion"];
        [posicion setValue:[dateFormatter dateFromString:dia] forKey:@"fecha"];
    }
    
    [posicion setValue:numPersonas forKey:@"numPersonas"];
    [posicion setValue:[timeFormatter dateFromString:hora] forKey:@"hora"];
    
    if(![moc save:&error]){
        NSLog(@"Error %@", [error localizedDescription]);
    }
}

/**
 *  Método que mueve a la persona según la variación
 */
- (void) moverPersona:(NSInteger)variacion
{
    [personaMovimiento removeFromSuperview];
    
    if(variacion >= 0){
        personaMovimiento = [UIImageView initPersonaSube:variacion];
        [_fondoImageView addSubview:personaMovimiento];
        
        [personaMovimiento subirPersona];
    }
    else {
        personaMovimiento = [UIImageView initPersonaBaja:variacion];
        [_fondoImageView addSubview:personaMovimiento];
        
        [personaMovimiento bajarPersona];
    }
}

/**
 *  Método que reinicia la pantalla para permitir que se comparta la posición
 */
- (void) reiniciarAVistaVacia
{
    numPersonasAnterior = 0;
    
    [_fondoImageView setImage:[UIImage initWithPersonas:0]];
    
    [_twitearButton setHidden:YES];
}

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation
{
    [ReverseGeocoder reverseGeocode:newLocation.coordinate 
                         completion:^(NSMutableDictionary *posicionActual, NSError *error)
    {
        // Si no se ha recuperado la calle, la ciudad, el estado o el país no se considera como posición válida
        if ([posicionActual valueForKey:@"road"] == nil || [posicionActual valueForKey:@"city"] == nil ||
            [posicionActual valueForKey:@"state"] == nil || [posicionActual valueForKey:@"country"] == nil) {
            [_posicionLabel setText:NSLocalizedString(@"posicionDesconocida", nil)];
            [_getNumeroPersonasButton setHidden:YES];
            [_insertarPosicionUsuarioButton setHidden:NO];
            [_insertarPosicionUsuarioButton setEnabled:NO];
            
            [self reiniciarAVistaVacia];
        }
        else if (posicionACompartir == nil || 
                 ![[posicionACompartir valueForKey:@"road"] isEqual:[posicionActual valueForKey:@"road"]]) {
            posicionACompartir = posicionActual;
            [_posicionLabel setText:[[posicionActual valueForKey:@"road"] uppercaseString]];
            [_getNumeroPersonasButton setHidden:YES];
            [_insertarPosicionUsuarioButton setHidden:NO];
            [_insertarPosicionUsuarioButton setEnabled:YES];
            
            [self reiniciarAVistaVacia];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error
{
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"localizacionErrorTitulo", nil)
                                                    message:NSLocalizedString(@"localizacionErrorDesc", nil)
                                                   delegate:nil 
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
	NSLog(@"%@: %@", NSLocalizedString(@"localizacionErrorTitulo", nil), [error description]);
}

@end
