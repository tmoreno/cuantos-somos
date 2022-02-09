#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

enum {
    RequestStateReady = 0,
    RequestStateExecuting,
    RequestStateFinished
};
typedef NSUInteger RequestState;

@interface ReverseGeocoder : NSOperation

@property (nonatomic, retain) NSMutableData *responseData;
@property (nonatomic, retain) NSURLConnection *connection;
@property (nonatomic, retain) NSMutableURLRequest *request;
@property (nonatomic, readwrite) RequestState state;

@property (nonatomic, retain) NSTimer *timeoutTimer; // see http://stackoverflow.com/questions/2736967
@property (nonatomic, copy) void (^completionBlock)(NSMutableDictionary *posicionActual, NSError *error);

+ (ReverseGeocoder *)reverseGeocode:(CLLocationCoordinate2D)coordinate 
                         completion:(void (^)(NSMutableDictionary *posicionActual, NSError *error))block;

- (void)cancel;
- (void)startAsynchronous;

@end