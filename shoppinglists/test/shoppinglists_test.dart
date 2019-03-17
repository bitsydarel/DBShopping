import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shoppinglists/shoppinglists.dart';

void main() {
  const MethodChannel channel = MethodChannel('shoppinglists');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Shoppinglists.platformVersion, '42');
  });
}
