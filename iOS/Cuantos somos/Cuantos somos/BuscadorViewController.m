#import <QuartzCore/QuartzCore.h>
#import "BuscadorViewController.h"
#import "NSString+URLEncoding.h"

#ifdef DEBUG
#define urlBuscador @"http://192.168.1.2:8080/cuantossomos-rest/buscador?p="
#else
#define urlBuscador @"https://cuantossomos-rest.cloudfoundry.com/buscador?p="
#endif

static NSInteger const kTimeout = 5;
static NSInteger const kAltura4Pulgadas = 568;

@interface BuscadorViewController ()

@end

@implementation BuscadorViewController

@synthesize idPosicion = _idPosicion;
@synthesize calle = _calle;
@synthesize fecha = _fecha;
@synthesize numPersonas = _numPersonas;

@synthesize calleLabel = _calleLabel;
@synthesize numPersonasLabel = _numPersonasLabel;
@synthesize datePicker = _datePicker;
@synthesize buscarButton = _buscarButton;
@synthesize activityIndicatorView = _activityIndicatorView;
@synthesize botonPickerView = _botonPickerView;

- (id)initWithCoder:(NSCoder*)aDecoder 
{
    if(self = [super initWithCoder:aDecoder]) {
        [self setTitle:NSLocalizedString(@"cuantosfuimos", nil)];
        
        numberFormatter = [[NSNumberFormatter alloc] init];
        [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
        
        timestampFormatter = [[NSDateFormatter alloc] init];
        [timestampFormatter setDateFormat:@"dd/MM/yyyy HH:mm:ss"];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [_buscarButton setTitle:NSLocalizedString(@"buscar", nil) forState:UIControlStateNormal];
    [_numPersonasLabel setText:[self getNumPersonasLabel]];
    [_calleLabel setText:_calle];
    [_datePicker setDate:_fecha];
    [_datePicker setMaximumDate:[[NSDate alloc] init]];
    
    if([[UIScreen mainScreen] bounds].size.height == kAltura4Pulgadas){
        CGRect frame = [_botonPickerView frame];
        [_botonPickerView setFrame:CGRectMake(frame.origin.x,
                                              frame.origin.y,
                                              frame.size.width,
                                              394)];
        
        frame = [_buscarButton frame];
        [_buscarButton setFrame:CGRectMake(frame.origin.x,
                                           frame.origin.y + 88,
                                           frame.size.width,
                                           frame.size.height)];
        
        frame = [_datePicker frame];
        [_datePicker setFrame:CGRectMake(frame.origin.x,
                                         frame.origin.y + 88,
                                         frame.size.width,
                                         frame.size.height)];
    }
 
    activityIndicatorContainerView = [[UIView alloc] initWithFrame:self.view.bounds];
    [_activityIndicatorView setFrame:CGRectMake(121, 0, 77, 77)];
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
    [self setCalleLabel:nil];
    [self setNumPersonasLabel:nil];
    
    [self setDatePicker:nil];
    [self setBuscarButton:nil];
    [self setActivityIndicatorView:nil];
    [self setBotonPickerView:nil];
    [super viewDidUnload];
}

- (IBAction)done:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (NSString *)getNumPersonasLabel
{
    NSString *numPersonasLabel;
    if([_numPersonas integerValue] == 1){
        numPersonasLabel = NSLocalizedString(@"unapersona", nil);
    }
    else{
        numPersonasLabel = [NSString stringWithFormat:NSLocalizedString(@"npersonas", nil), 
                            [numberFormatter stringFromNumber:_numPersonas]];
    }

    return numPersonasLabel;
}

/**
 *  Método que consulta el número de personas que hay en la posición en la que estas
 */
- (IBAction) getNumeroPersonas:(id)sender
{
    NSMutableDictionary *numeroPersonasParams = [[NSMutableDictionary alloc] init];
    [numeroPersonasParams setValue:_idPosicion forKey:@"idPosicion"];
    [numeroPersonasParams setValue:[timestampFormatter stringFromDate:[_datePicker date]] forKey:@"fecha"];
    
    NSData *data = [NSJSONSerialization dataWithJSONObject:numeroPersonasParams 
                                                   options:kNilOptions 
                                                     error:nil];
    
    [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] urlEncode];
    
    NSString *url = [NSString stringWithFormat:@"%@%@", urlBuscador, [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] urlEncode]];
    
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

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[responseData setLength:0];
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
    
    NSError *error;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:responseData options:kNilOptions error:&error];
        
    _numPersonas = [json objectForKey:@"numPersonas"];
    
    [_numPersonasLabel setText:[self getNumPersonasLabel]];
}
@end
