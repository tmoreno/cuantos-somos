#import "CustomUITabBarController.h"

@interface CustomUITabBarController ()

@end

@implementation CustomUITabBarController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    return [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	NSArray *items = [[self tabBar] items];
    [[items objectAtIndex:0] setTitle:NSLocalizedString(@"cuantossomos", nil)];
    [[items objectAtIndex:1] setTitle:NSLocalizedString(@"lugares", nil)];
    [[items objectAtIndex:2] setTitle:NSLocalizedString(@"acercade", nil)];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

@end
