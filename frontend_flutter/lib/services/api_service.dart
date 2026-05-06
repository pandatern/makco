import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/models.dart';

class ApiService {
  static const String baseUrl = 'https://api.pandatern.tech';
  
  String? _token;
  
  void setToken(String token) => _token = token;

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (_token != null) 'token': _token!,
  };

  Future<Map<String, dynamic>> initiateAuth(String mobileNumber) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth'),
      headers: _headers,
      body: jsonEncode({
        'mobileNumber': mobileNumber,
        'mobileCountryCode': '+91',
      }),
    );
    return jsonDecode(response.body);
  }

  Future<Map<String, dynamic>> verifyAuth(String authId, String otp) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/$authId/verify'),
      headers: _headers,
      body: jsonEncode({'otp': otp}),
    );
    return jsonDecode(response.body);
  }

  Future<List<Station>> getStations() async {
    final response = await http.get(
      Uri.parse('$baseUrl/metro/stations?city=chennai'),
      headers: _headers,
    );
    final List data = jsonDecode(response.body);
    return data.map((s) => Station.fromJson(s)).toList();
  }

  Future<Map<String, dynamic>> searchFare(String fromCode, String toCode, {int quantity = 1}) async {
    final response = await http.post(
      Uri.parse('$baseUrl/metro/search?city=chennai'),
      headers: _headers,
      body: jsonEncode({
        'fromStationCode': fromCode,
        'toStationCode': toCode,
        'quantity': quantity,
      }),
    );
    return jsonDecode(response.body);
  }

  Future<List<Quote>> getQuotes(String searchId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/metro/search/$searchId/quote?city=chennai'),
      headers: _headers,
    );
    final List data = jsonDecode(response.body);
    return data.map((q) => Quote.fromJson(q)).toList();
  }

  Future<BookingStatus> confirmBooking(String quoteId, {int quantity = 1}) async {
    final response = await http.post(
      Uri.parse('$baseUrl/metro/quote/$quoteId/confirm?city=chennai'),
      headers: _headers,
      body: jsonEncode({'quantity': quantity}),
    );
    return BookingStatus.fromJson(jsonDecode(response.body));
  }

  Future<BookingStatus> getBookingStatus(String bookingId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/booking/$bookingId/status?city=chennai'),
      headers: _headers,
    );
    return BookingStatus.fromJson(jsonDecode(response.body));
  }

  Future<BookingStatus> refreshBookingStatus(String bookingId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/booking/$bookingId/refresh?city=chennai'),
      headers: _headers,
    );
    return BookingStatus.fromJson(jsonDecode(response.body));
  }

  Future<List<BookingStatus>> getTicketBookings() async {
    final response = await http.get(
      Uri.parse('$baseUrl/tickets?city=chennai'),
      headers: _headers,
    );
    final List data = jsonDecode(response.body);
    return data.map((b) => BookingStatus.fromJson(b)).toList();
  }
}
