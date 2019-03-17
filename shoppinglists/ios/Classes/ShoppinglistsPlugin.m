#import "ShoppinglistsPlugin.h"
#import <shoppinglists/shoppinglists-Swift.h>

@implementation ShoppinglistsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftShoppinglistsPlugin registerWithRegistrar:registrar];
}
@end
