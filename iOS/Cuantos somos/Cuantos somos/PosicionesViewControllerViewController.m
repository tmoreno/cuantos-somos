#import "PosicionesViewControllerViewController.h"
#import "NumPersonasPosicionViewController.h"
#import "TMBAppDelegate.h"

#define INDICE_MIN_ITEMS 40
@interface PosicionesViewControllerViewController ()

@end

@implementation PosicionesViewControllerViewController

@synthesize posicionesFiltradas = _posicionesFiltradas;
@synthesize posicionesSearchBar = _posicionesSearchBar;

- (id)initWithCoder:(NSCoder*)aDecoder 
{
    if(self = [super initWithCoder:aDecoder]) {
        [self setTitle:NSLocalizedString(@"lugares", nil)];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    CGRect bounds = self.tableView.bounds;
    bounds.origin.y = bounds.origin.y + _posicionesSearchBar.bounds.size.height;
    self.tableView.bounds = bounds;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _posicionesFiltradas = nil;
    _posicionesSearchBar = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [self loadTableView];
}

- (void) loadTableView
{
    TMBAppDelegate *delegado = [TMBAppDelegate sharedAppController];
    
    posiciones = [NSMutableArray arrayWithArray:[delegado allInstancesOf:@"Posicion" orderBy:@"calle"]];
    
    if([posiciones count] > INDICE_MIN_ITEMS){
        ponerIndice = YES;
        posiciones = [self reloadPosiciones:posiciones];
    }
    else{
        ponerIndice = NO;
    }
    
    [[self tableView] reloadData];
}

- (NSMutableArray *)reloadPosiciones:(NSArray *)posicionesToReload
{
    NSString *inicialActual;
    NSString *inicialAnterior = @"";
    NSInteger indexInicialActual;
    NSMutableDictionary *seccion;
    NSMutableArray *posicionesAux;
    
    NSMutableArray *resultado = [[NSMutableArray alloc] init];
    
    for(NSManagedObject *posicion in posicionesToReload){
        inicialActual = [[posicion valueForKey:@"calle"] substringToIndex:1];
        
        if(![inicialActual compare:inicialAnterior
                           options:NSDiacriticInsensitiveSearch | NSCaseInsensitiveSearch] == NSOrderedSame){
            inicialAnterior = inicialActual;
            
            if([[resultado valueForKey:@"tituloSeccion"] containsObject:inicialActual]){
                indexInicialActual = [[resultado valueForKey:@"tituloSeccion"] indexOfObject:inicialActual];
                seccion = [resultado objectAtIndex:indexInicialActual];
                posicionesAux = [seccion valueForKey:@"posiciones"];
                
                [posicionesAux addObject:posicion];
            }
            else{
                seccion = [[NSMutableDictionary alloc] init];
                posicionesAux = [[NSMutableArray alloc] init];
                
                [posicionesAux addObject:posicion];
                [seccion setValue:inicialActual forKey:@"tituloSeccion"];
                [seccion setValue:posicionesAux forKey:@"posiciones"];
                
                [resultado addObject:seccion];
            }
        }
        else{
            [[seccion objectForKey:@"posiciones"] addObject:posicion];
        }
    }
    
    return resultado;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    NSInteger numSections = 1;
    
    if(ponerIndice && tableView != self.searchDisplayController.searchResultsTableView){
        numSections = [posiciones count];
    }

    return numSections;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSInteger rowsInSection = [posiciones count];
    
    if (tableView == self.searchDisplayController.searchResultsTableView) {
        rowsInSection = [_posicionesFiltradas count];
    } 
    else if(ponerIndice){
        rowsInSection = [[[posiciones objectAtIndex:section] objectForKey:@"posiciones"] count];
    }
    
    return rowsInSection;
}

- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView 
{
    NSArray *indexTitles = nil;
    
    if(ponerIndice && tableView != self.searchDisplayController.searchResultsTableView){
        indexTitles = [posiciones valueForKey:@"tituloSeccion"];
    }
    
    return indexTitles;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index 
{
    return [[posiciones valueForKey:@"tituloSeccion"] indexOfObject:title];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	NSString *headerTitle = nil;
    
    if(ponerIndice && tableView != self.searchDisplayController.searchResultsTableView){
        headerTitle = [[posiciones objectAtIndex:section] objectForKey:@"tituloSeccion"];
    }
    
    return headerTitle;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if(!cell){
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"Cell"];
    }
    
    NSManagedObject *posicion;
    if (tableView == self.searchDisplayController.searchResultsTableView) {
        posicion = [_posicionesFiltradas objectAtIndex:indexPath.row];
        [cell setAccessoryType:UITableViewCellAccessoryDisclosureIndicator];
    } 
    else {
        posicion = [posiciones objectAtIndex:indexPath.row];
        
        if(ponerIndice){
            posicion = [[[posiciones objectAtIndex:indexPath.section] objectForKey:@"posiciones"] 
                        objectAtIndex:indexPath.row];
            
            [cell setAccessoryType:UITableViewCellAccessoryNone];
        }
        else{
            [cell setAccessoryType:UITableViewCellAccessoryDisclosureIndicator];
        }
    }
    
    [[cell textLabel] setText:[posicion valueForKey:@"calle"]];
    [[cell detailTextLabel] setText:[posicion valueForKey:@"ciudad"]];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 46;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
                                            forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        NSManagedObject *posicionABorrar;
        
        if (tableView == self.searchDisplayController.searchResultsTableView) {
            posicionABorrar = [_posicionesFiltradas objectAtIndex:indexPath.row];
            [_posicionesFiltradas removeObjectAtIndex:indexPath.row];
            borradaPosicionFiltro = YES;
        }
        else{
            if (ponerIndice) {
                posicionABorrar = [[[posiciones objectAtIndex:indexPath.section] objectForKey:@"posiciones"] 
                                   objectAtIndex:indexPath.row];
                [[[posiciones objectAtIndex:indexPath.section] objectForKey:@"posiciones"] 
                 removeObjectAtIndex:indexPath.row];
            }
            else{
                posicionABorrar = [posiciones objectAtIndex:indexPath.row];
                [posiciones removeObjectAtIndex:indexPath.row];
            }
        }
                
        TMBAppDelegate *delegado = [TMBAppDelegate sharedAppController];
        [delegado deletePosicion:posicionABorrar];
        
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] 
                         withRowAnimation:UITableViewRowAnimationFade];
    }   
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Perform segue to candy detail
    [self performSegueWithIdentifier:@"numPersonasPosicion" sender:tableView];
}


-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"numPersonasPosicion"]) {
        NumPersonasPosicionViewController *numPersonasPosicionViewController = [segue destinationViewController];
        NSManagedObject *posicion;

        if(sender == self.searchDisplayController.searchResultsTableView) {
            NSIndexPath *indexPath = [self.searchDisplayController.searchResultsTableView indexPathForSelectedRow];
            posicion = [_posicionesFiltradas objectAtIndex:[indexPath row]];
        }
        else {
            NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
            if(ponerIndice){
                posicion = [[[posiciones objectAtIndex:indexPath.section] objectForKey:@"posiciones"] 
                                    objectAtIndex:indexPath.row];
            }
            else {
                posicion = [posiciones objectAtIndex:[indexPath row]];
            }
        }
    
        [numPersonasPosicionViewController setPosicion:posicion];
        [numPersonasPosicionViewController setTitle:[posicion valueForKey:@"calle"]];
    }
}

-(void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope 
{
    [_posicionesFiltradas removeAllObjects];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF.calle contains[c] %@", searchText];
    
    if(ponerIndice){
        NSMutableArray *aux = [[NSMutableArray alloc] init];
        for(NSDictionary *dic in posiciones){
            [aux addObjectsFromArray:[dic valueForKey:@"posiciones"]]; 
        }
        _posicionesFiltradas = [NSMutableArray arrayWithArray:[aux filteredArrayUsingPredicate:predicate]];
    }
    else{
        _posicionesFiltradas = [NSMutableArray arrayWithArray:[posiciones filteredArrayUsingPredicate:predicate]];
    }
}

-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    
    [self filterContentForSearchText:searchString scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:[self.searchDisplayController.searchBar selectedScopeButtonIndex]]];
    
    return YES;
}

-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchScope:(NSInteger)searchOption {
   
    [self filterContentForSearchText:self.searchDisplayController.searchBar.text scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:searchOption]];
    
    return YES;
}

// Si el item que se ha eliminado es del buscador, hay que refrescar
// la tabla principal
- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar
{	
    if (borradaPosicionFiltro) {
        [self loadTableView];
        borradaPosicionFiltro = NO;
    }
}

@end
