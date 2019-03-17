import 'dart:async';

import 'package:flutter/services.dart';

class Shoppinglists {
  static const MethodChannel _channel =
      const MethodChannel('shoppinglists');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
