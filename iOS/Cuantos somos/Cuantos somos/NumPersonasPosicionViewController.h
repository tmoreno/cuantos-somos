#import <UIKit/UIKit.h>

@interface NumPersonasPosicionViewController : UITableViewController
{
    NSNumberFormatter *numberFormatter;
    NSDateFormatter *dateFormatter;
    NSDateFormatter *timeFormatter;

    NSMutableArray *numPersonasPosicion;
}

@property (nonatomic, strong) NSManagedObject *posicion;

@end
