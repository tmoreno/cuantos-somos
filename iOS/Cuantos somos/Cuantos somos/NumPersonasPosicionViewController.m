#import "NumPersonasPosicionViewController.h"
#import "BuscadorViewController.h"
#import "TMBAppDelegate.h"

@interface NumPersonasPosicionViewController ()

@end

@implementation NumPersonasPosicionViewController

@synthesize posicion = _posicion;

- (id)initWithCoder:(NSCoder*)aDecoder 
{
    if(self = [super initWithCoder:aDecoder]) {
        numberFormatter = [[NSNumberFormatter alloc] init];
        [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
        
        dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"dd/MM/yyyy"];
        
        timeFormatter = [[NSDateFormatter alloc] init];
        [timeFormatter setDateFormat:@"HH:mm"];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _posicion = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    TMBAppDelegate *delegado = [TMBAppDelegate sharedAppController];
    
    numPersonasPosicion = [NSMutableArray arrayWithArray:[delegado getNumPersonasPosicion:[_posicion valueForKey:@"idPosicion"]]];
    
    [[self tableView] reloadData];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [numPersonasPosicion count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if(!cell){
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"Cell"];
    }
    
    NSManagedObject *numPersonas = [numPersonasPosicion objectAtIndex:indexPath.row];
    
    NSString *numPersonasText;
    if([[numPersonas valueForKey:@"numPersonas"] integerValue] > 1){
        numPersonasText = [NSString stringWithFormat:NSLocalizedString(@"npersonas", nil), 
                           [numberFormatter stringFromNumber:[numPersonas valueForKey:@"numPersonas"]]];
    }
    else{
        numPersonasText = NSLocalizedString(@"unapersona", nil);
    }
    
    NSString *fecha = [NSString stringWithFormat:@"%@ %@", 
                       [dateFormatter stringFromDate:[numPersonas valueForKey:@"fecha"]], 
                       [timeFormatter stringFromDate:[numPersonas valueForKey:@"hora"]]];
    
    [[cell textLabel] setText:fecha];
    [[cell detailTextLabel] setText:numPersonasText];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 46;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        NSError* error;
        
        TMBAppDelegate *delegado = [TMBAppDelegate sharedAppController];
        NSManagedObjectContext *moc = [delegado managedObjectContext];
        
        NSManagedObject *numPersonasABorrar = [numPersonasPosicion objectAtIndex:indexPath.row];
        [numPersonasPosicion removeObjectAtIndex:indexPath.row];
        
        [moc deleteObject:numPersonasABorrar];
        if(![moc save:&error]){
            NSLog(@"Error %@", [error localizedDescription]);
        }
        
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] 
                         withRowAnimation:UITableViewRowAnimationFade];
    }
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"buscador"]) {
        UINavigationController *navigationController = [segue destinationViewController];
        BuscadorViewController *buscadorController = (BuscadorViewController *)[navigationController topViewController];
        
        [buscadorController setIdPosicion:[_posicion valueForKey:@"idPosicion"]];
        [buscadorController setCalle:[_posicion valueForKey:@"calle"]];
        
        if([numPersonasPosicion count] != 0){
            [buscadorController setFecha:[[numPersonasPosicion objectAtIndex:0] valueForKey:@"fecha"]];
            [buscadorController setNumPersonas:[[numPersonasPosicion objectAtIndex:0] valueForKey:@"numPersonas"]];
        }
        else{
            [buscadorController setFecha:[[NSDate alloc] init]];
            [buscadorController setNumPersonas:[NSNumber numberWithInt:0]];
        }
    }
}

@end
