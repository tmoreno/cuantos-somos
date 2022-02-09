#import "UIImage+ImagenPersonas.h"

@implementation UIImage (ImagenPersonas)

/**
 *  Cambia el fondo de la app según el número de personas que haya en ese momento
 */
+ (UIImage *) initWithPersonas:(NSInteger) personas
{
    UIImage *fondo = nil;
    
    if(personas >= 1 && personas <= 100){
        fondo = [UIImage imageNamed:@"fondo_1_100.jpg"];
    }
    else if(personas >= 101 && personas <= 1000){
        fondo = [UIImage imageNamed:@"fondo_101_1000.jpg"];
    }
    else if(personas >= 1001 && personas <= 10000){
        fondo = [UIImage imageNamed:@"fondo_1001_10000.jpg"];
    }
    else if(personas >= 10001 && personas <= 100000){
        fondo = [UIImage imageNamed:@"fondo_10001_100000.jpg"];
    }
    else if(personas > 100000){
        fondo = [UIImage imageNamed:@"fondo_100000_mas.jpg"];
    }
    else {
        fondo = [UIImage imageNamed:@"fondo_vacio.jpg"];
    }
    
    return fondo;
}
@end
