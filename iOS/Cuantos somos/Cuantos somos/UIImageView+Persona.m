#import "UIImageView+Persona.h"

static NSInteger const kAltura4Pulgadas = 568;

@implementation UIImageView (Persona)

/**
 *  Método que posiciona a la persona abajo de la pantalla para que suba al grupo
 */
+ (UIImageView *) initPersonaSube:(NSInteger) variacion
{
    NSInteger origenY = 420;
    
    if([[UIScreen mainScreen] bounds].size.height == kAltura4Pulgadas){
        origenY = 530;
    }
    
    return [UIImageView initPersona:origenY variacion:variacion];
}

/**
 *  Método que posiciona a la persona dentro del grupo para que salga de él
 */
+ (UIImageView *) initPersonaBaja:(NSInteger) variacion
{
    return [UIImageView initPersona:220 variacion:variacion];
}

/**
 *  Método que construye la imagen de la persona con el cartel de la variación del número de personas
 *  y la posiciona verticalmente según el valor de 'y'
 */
+ (UIImageView *) initPersona:(NSInteger) y 
                    variacion:(NSInteger) variacion
{
    NSArray *personaFrames = [[NSArray alloc] initWithObjects:
                              [UIImage imageNamed:@"persona_izquierda.png"],
                              [UIImage imageNamed:@"persona_derecha.png"],
                              nil];
    
    UIImageView *personaImageView = [[UIImageView alloc] init];
    [personaImageView setFrame:CGRectMake(140, y, 44, 112)];
    [personaImageView setAnimationImages:personaFrames];
    [personaImageView setAnimationDuration:1];
    [personaImageView setAnimationRepeatCount:0];
    
    // Esta view es necesaria para que la label tenga un padding
    UIView *variacionView = [[UIView alloc] initWithFrame:CGRectMake(-14, 30, 70, 30)];
    [variacionView setBackgroundColor:[UIColor whiteColor]];
    [[variacionView layer] setBorderWidth:1.0];
    [[variacionView layer] setCornerRadius:8];
    [personaImageView addSubview: variacionView];
    
    UILabel *variacionLabel = [[UILabel alloc] initWithFrame: CGRectMake(20, 0, 45, 30)];
    if(abs(variacion) <= 100){
        [variacionLabel setText:[NSString stringWithFormat:@"%d", abs(variacion)]];
    }
    else{
        [variacionLabel setText:@"+100"];
    }
    [variacionLabel setTextAlignment:UITextAlignmentRight];
    [[variacionLabel layer] setCornerRadius:8];
    [variacionView addSubview: variacionLabel];
    
    UIImage *indicador = nil;
    if(variacion > 0){
        indicador = [UIImage imageNamed:@"sube.png"];
    }
    else if (variacion < 0) {
        indicador = [UIImage imageNamed:@"baja.png"];
    }
    else {
        indicador = [UIImage imageNamed:@"igual.png"];
    }
    UIImageView *indicadorImageView = [[UIImageView alloc] initWithImage:indicador];
    indicadorImageView.frame = CGRectMake(3, 10, indicador.size.width, indicador.size.height);
    [variacionView addSubview:indicadorImageView];

    return personaImageView;
}

- (void) subirPersona
{
    NSInteger restarY = -200;
    
    if([[UIScreen mainScreen] bounds].size.height == kAltura4Pulgadas){
        restarY = -310;
    }
    
    [self moverPersona:restarY];
}

- (void) bajarPersona
{
    NSInteger sumarY = 120;
    
    if([[UIScreen mainScreen] bounds].size.height == kAltura4Pulgadas){
        sumarY = 210;
    }

    [self moverPersona:sumarY];
}

- (void) moverPersona:(NSInteger) y
{
    NSTimeInterval duracion = 3.0;
    if([[UIScreen mainScreen] bounds].size.height == kAltura4Pulgadas){
        duracion = 5.0;
    }
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:duracion];
    [UIView setAnimationCurve:UIViewAnimationCurveLinear];
    [UIView setAnimationDelegate:self];
    [self startAnimating];
    [self setTransform:CGAffineTransformMakeTranslation(0, y)];
    [UIView commitAnimations];
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag
{
    [self stopAnimating];
    [self setImage:[UIImage imageNamed:@"persona_pies_juntos.png"]];
}
@end
