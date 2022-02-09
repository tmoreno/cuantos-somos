#import "TMBAppDelegate.h"
#import "CompartirUbicacionViewController.h"
#import "GAI.h"

static TMBAppDelegate *sharedInstance;

@implementation TMBAppDelegate

@synthesize window = _window;
@synthesize managedObjectContext = __managedObjectContext;
@synthesize managedObjectModel = __managedObjectModel;
@synthesize persistentStoreCoordinator = __persistentStoreCoordinator;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    sharedInstance = self;
    
    // Se muestra la barra de estado porque la pantalla de splash la oculta
    [[UIApplication sharedApplication] setStatusBarHidden:NO];
    
    // Optional: automatically track uncaught exceptions with Google Analytics.
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    
    // Optional: set Google Analytics dispatch interval to e.g. 20 seconds.
    [GAI sharedInstance].dispatchInterval = 20;
    
    // Create tracker instance.
    [[GAI sharedInstance] trackerWithTrackingId:@"UA-33663391-2"];
    
    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
     */
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    /*
     * Called as part of the transition from the background to the inactive state; here you can undo many of the changes 
     * made on entering the background.
     */
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    /*
     Called when the application is about to terminate.
     Save data if appropriate.
     See also applicationDidEnterBackground:.
     */
}

+ (TMBAppDelegate *) sharedAppController
{
    return sharedInstance;
}

/**
 * Método que recupera una posición
 */
- (NSManagedObject *) getPosicion:(NSNumber *)identify
{
    NSError *error;
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"Posicion" 
                                                         inManagedObjectContext:[self managedObjectContext]];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idPosicion = %@", identify];
    [request setPredicate:predicate];
    
    NSArray *resultado = [[self managedObjectContext] executeFetchRequest:request error:&error];
    
    if([resultado count] == 0){
        return nil;
    }
    else{
        return [resultado objectAtIndex:0];
    }
}

/**
 * Método que recupera el número de personas que había en una posición dada una fecha
 */
- (NSManagedObject *) getPosicion:(NSNumber *)identify byDate:(NSDate *) fecha
{
    NSError *error;
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"NumPersonasPosicion" 
                                                         inManagedObjectContext:[self managedObjectContext]];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idPosicion = %@ and fecha = %@", identify, fecha];
    [request setPredicate:predicate];
    
    NSArray *resultado = [[self managedObjectContext] executeFetchRequest:request error:&error];
    
    if([resultado count] == 0){
        return nil;
    }
    else{
        return [resultado objectAtIndex:0];
    }
}

/**
 * Método que recupera todas las instancias de una entidad ordenadas por un atributo
 */
- (NSArray *)allInstancesOf:(NSString *)entityName orderBy:(NSString *)attName
{
    NSError* error;
    NSEntityDescription *entity = [NSEntityDescription entityForName:entityName 
                                              inManagedObjectContext:[self managedObjectContext]];
    
    NSFetchRequest *fetch = [[NSFetchRequest alloc] init];
    [fetch setEntity:entity];
    
    if(attName){
        NSSortDescriptor *sorter = [[NSSortDescriptor alloc] initWithKey:attName 
                                                               ascending:YES
                                                                selector:@selector(localizedCaseInsensitiveCompare:)];
        NSArray *sortDescriptos = [NSArray arrayWithObject:sorter];
        
        [fetch setSortDescriptors:sortDescriptos];
    }
    
    NSArray *result = [[self managedObjectContext] executeFetchRequest:fetch error:&error];
    
    return result;
}

/**
 * Método que devuelve una lista con el número de personas que había en una posición según el día
 */
- (NSArray *) getNumPersonasPosicion:(NSNumber *)idPosicion
{
    NSError *error;
    NSEntityDescription *entityDescription = [NSEntityDescription entityForName:@"NumPersonasPosicion" 
                                                         inManagedObjectContext:[self managedObjectContext]];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDescription];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idPosicion = %@", idPosicion];
    [request setPredicate:predicate];
    
    NSSortDescriptor *sorterFecha = [[NSSortDescriptor alloc] initWithKey:@"fecha" ascending:NO];
    NSArray *sortDescriptos = [NSArray arrayWithObject:sorterFecha];
    
    [request setSortDescriptors:sortDescriptos];

    NSArray *resultado = [[self managedObjectContext] executeFetchRequest:request error:&error];
    
    return resultado;
}

/**
 * Método que borra una posición y los distintos números de personas que había en esa posición según el día
 */
- (void) deletePosicion:(NSManagedObject *)posicion
{
    NSError *error;
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:[NSEntityDescription entityForName:@"NumPersonasPosicion" 
                                   inManagedObjectContext:[self managedObjectContext]]];
    [request setIncludesPropertyValues:NO];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idPosicion = %@", [posicion valueForKey:@"idPosicion"]];
    [request setPredicate:predicate];
    
    NSArray *numPersonas = [[self managedObjectContext] executeFetchRequest:request error:&error];
   
    for (NSManagedObject *item in numPersonas) {
        [[self managedObjectContext] deleteObject:item];
    }
    
    [[self managedObjectContext] deleteObject:posicion];
    
    [[self managedObjectContext] save:&error];
}

- (void)saveContext
{
    NSError *error = nil;
    NSManagedObjectContext *managedObjectContext = self.managedObjectContext;
    if (managedObjectContext != nil) {
        if ([managedObjectContext hasChanges] && ![managedObjectContext save:&error]) {
            // Replace this implementation with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development. 
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        } 
    }
}

// Returns the managed object context for the application.
// If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
- (NSManagedObjectContext *)managedObjectContext
{
    if (__managedObjectContext != nil) {
        return __managedObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (coordinator != nil) {
        __managedObjectContext = [[NSManagedObjectContext alloc] init];
        [__managedObjectContext setPersistentStoreCoordinator:coordinator];
    }
    return __managedObjectContext;
}

// Returns the managed object model for the application.
// If the model doesn't already exist, it is created from the application's model.
- (NSManagedObjectModel *)managedObjectModel
{
    if (__managedObjectModel != nil) {
        return __managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"Modelo" withExtension:@"momd"];
    __managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return __managedObjectModel;
}

// Returns the persistent store coordinator for the application.
// If the coordinator doesn't already exist, it is created and the application's store added to it.
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
    if (__persistentStoreCoordinator != nil) {
        return __persistentStoreCoordinator;
    }
    
    NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"Modelo.sqlite"];
    
    NSError *error = nil;
    __persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    if (![__persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error]) {
        /*
         Replace this implementation with code to handle the error appropriately.
         
         abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development. 
         
         Typical reasons for an error here include:
         * The persistent store is not accessible;
         * The schema for the persistent store is incompatible with current managed object model.
         Check the error message to determine what the actual problem was.
         
         
         If the persistent store is not accessible, there is typically something wrong with the file path. Often, a file URL is pointing into the application's resources directory instead of a writeable directory.
         
         If you encounter schema incompatibility errors during development, you can reduce their frequency by:
         * Simply deleting the existing store:
         [[NSFileManager defaultManager] removeItemAtURL:storeURL error:nil]
         
         * Performing automatic lightweight migration by passing the following dictionary as the options parameter: 
         [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES], NSMigratePersistentStoresAutomaticallyOption, [NSNumber numberWithBool:YES], NSInferMappingModelAutomaticallyOption, nil];
         
         Lightweight migration will only work for a limited set of schema changes; consult "Core Data Model Versioning and Data Migration Programming Guide" for details.
         
         */
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }    
    
    return __persistentStoreCoordinator;
}

// Returns the URL to the application's Documents directory.
- (NSURL *)applicationDocumentsDirectory
{
    return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}

@end
