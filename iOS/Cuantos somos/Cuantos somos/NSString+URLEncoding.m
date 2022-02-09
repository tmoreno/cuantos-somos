#import "NSString+URLEncoding.h"

@implementation NSString (URLEncoding)
- (NSString *) urlEncode 
{
	return (__bridge_transfer NSString *)CFURLCreateStringByAddingPercentEscapes(NULL,
                                                               (__bridge CFStringRef)self,
                                                               NULL,
                                                               (CFStringRef)@"!*'\"();:@&=+$,/?%#[]% ",
                                                               kCFStringEncodingUTF8);
}
@end
