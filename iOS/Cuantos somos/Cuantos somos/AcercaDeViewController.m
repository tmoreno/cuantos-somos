#import "AcercaDeViewController.h"

@interface AcercaDeViewController ()

@end

@implementation AcercaDeViewController

- (id)initWithCoder:(NSCoder*)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        [self setTitle:NSLocalizedString(@"acercade", nil)];
    }
    
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return NSLocalizedString(@"siguenos", nil);
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
    return NSLocalizedString(@"ayudadifundir", nil);
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *url;
    
    if(indexPath.row == 0){
        url = @"http://www.facebook.com/pages/Cuantos-somos/270303983084604";
    }
    else if(indexPath.row == 1){
        url = @"https://twitter.com/cuantos_somos";
    }
    else{
        url = @"http://www.cuantos-somos.com";
    }
    
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES]; 
}

@end
