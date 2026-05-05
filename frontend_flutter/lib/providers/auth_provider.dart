import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/api_service.dart';

class AuthProvider extends ChangeNotifier {
  final ApiService _apiService = ApiService();
  String? _token;
  String? _phone;
  bool _isAuthenticated = false;

  String? get token => _token;
  bool get isAuthenticated => _isAuthenticated;
  ApiService get apiService => _apiService;

  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('auth_token');
    _phone = prefs.getString('auth_phone');
    if (_token != null) {
      _apiService.setToken(_token!);
      _isAuthenticated = true;
      notifyListeners();
    }
  }

  Future<Map<String, dynamic>> initiateAuth(String phone) async {
    _phone = phone;
    return await _apiService.initiateAuth(phone);
  }

  Future<bool> verifyOtp(String authId, String otp) async {
    try {
      final response = await _apiService.verifyAuth(authId, otp);
      if (response.containsKey('token')) {
        _token = response['token'];
        _apiService.setToken(_token!);
        _isAuthenticated = true;
        
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('auth_token', _token!);
        if (_phone != null) await prefs.setString('auth_phone', _phone!);
        
        notifyListeners();
        return true;
      }
    } catch (e) {
      debugPrint('Verify Error: $e');
    }
    return false;
  }

  Future<void> logout() async {
    _token = null;
    _isAuthenticated = false;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('auth_token');
    notifyListeners();
  }
}
