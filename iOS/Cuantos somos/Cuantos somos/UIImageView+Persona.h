#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

@interface UIImageView (Persona)
+ (UIImageView *) initPersonaSube:(NSInteger) variacion;
+ (UIImageView *) initPersonaBaja:(NSInteger) variacion;

- (void) subirPersona;
- (void) bajarPersona;
@end
