#import <Accounts/Accounts.h>
#import <UIKit/UIKit.h>

@interface TMBAppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

- (void) saveContext;
- (NSURL *) applicationDocumentsDirectory;
- (NSManagedObject *) getPosicion:(NSNumber *)identify;
- (NSManagedObject *) getPosicion:(NSNumber *)identify byDate:(NSDate *) fecha;
- (NSArray *) allInstancesOf:(NSString *)entityName orderBy:(NSString *) attName;
- (NSArray *) getNumPersonasPosicion:(NSNumber *)idPosicion;
- (void) deletePosicion:(NSManagedObject *)posicion;

+ (TMBAppDelegate *) sharedAppController;

@end
