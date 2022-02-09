#import <CoreLocation/CoreLocation.h>

@interface CompartirUbicacionViewController : UIViewController <CLLocationManagerDelegate> {
    NSNumberFormatter *numberFormatter;
    NSDateFormatter *timestampFormatter;
    NSDateFormatter *dateFormatter;
    NSDateFormatter *timeFormatter;
    
    CLLocationManager *locationManager;
    
    NSMutableData *responseData;
    NSURLConnection *posicionConnection;
    NSInteger httpStatusCode;
    
    UIImageView *personaMovimiento;
    
    NSMutableDictionary *posicionACompartir;
    NSMutableDictionary *numeroPersonasParams;
    NSNumber *idPosicion;
    NSNumber *numPersonas;
    NSNumber *numPersonasAnterior;
    
    UIView *activityIndicatorContainerView;
}

@property (nonatomic, strong) CLLocationManager *locationManager;

@property (nonatomic, weak) IBOutlet UIImageView *fondoImageView;
@property (nonatomic, weak) IBOutlet UILabel *posicionLabel;
@property (nonatomic, weak) IBOutlet UIButton *insertarPosicionUsuarioButton;
@property (nonatomic, weak) IBOutlet UIButton *getNumeroPersonasButton;
@property (nonatomic, weak) IBOutlet UIButton *twitearButton;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView *activityIndicatorView;

- (IBAction) insertarPosicionUsuario:(id)sender;
- (IBAction) getNumeroPersonas:(id)sender;
- (IBAction) twitearNumeroPersonas:(id)sender;

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation;

- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error;

@end
