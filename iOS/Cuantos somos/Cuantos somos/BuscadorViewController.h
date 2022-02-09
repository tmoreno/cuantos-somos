#import <UIKit/UIKit.h>

@interface BuscadorViewController : UITableViewController{
    NSNumberFormatter *numberFormatter;
    NSDateFormatter *timestampFormatter;
    
    NSMutableData *responseData;
    
    UIView *activityIndicatorContainerView;
}

@property (nonatomic, strong) NSNumber *idPosicion;
@property (nonatomic, strong) NSString *calle;
@property (nonatomic, strong) NSDate *fecha;
@property (nonatomic, strong) NSNumber *numPersonas;

@property (weak, nonatomic) IBOutlet UILabel *calleLabel;
@property (weak, nonatomic) IBOutlet UILabel *numPersonasLabel;
@property (weak, nonatomic) IBOutlet UIDatePicker *datePicker;
@property (weak, nonatomic) IBOutlet UIButton *buscarButton;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicatorView;
@property (weak, nonatomic) IBOutlet UIView *botonPickerView;

@end
