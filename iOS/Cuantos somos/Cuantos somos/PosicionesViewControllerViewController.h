#import <UIKit/UIKit.h>

@interface PosicionesViewControllerViewController : UITableViewController <UISearchBarDelegate, UISearchDisplayDelegate>
{    
    Boolean ponerIndice;
    Boolean borradaPosicionFiltro;
    NSMutableArray *posiciones;
}

@property (nonatomic, strong) NSMutableArray *posicionesFiltradas;
@property (nonatomic, weak) IBOutlet UISearchBar *posicionesSearchBar;

@end
