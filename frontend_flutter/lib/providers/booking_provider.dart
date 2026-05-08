import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/models.dart';
import '../services/api_service.dart';

class BookingProvider extends ChangeNotifier {
  final ApiService _api;
  
  List<Station> stations = [];
  List<Station> recentStations = [];
  Station? sourceStation;
  Station? destinationStation;
  List<Quote> quotes = [];
  BookingStatus? currentBooking;
  bool isLoading = false;
  String? error;

  BookingProvider(this._api);

  Future<void> fetchStations() async {
    isLoading = true;
    notifyListeners();
    try {
      stations = await _api.getStations();
      await fetchRecentStations();
      error = null;
    } catch (e) {
      error = "Failed to load stations";
    }
    isLoading = false;
    notifyListeners();
  }

  Future<void> fetchRecentStations() async {
    final prefs = await SharedPreferences.getInstance();
    final List<String> saved = prefs.getStringList('recent_stations') ?? [];
    recentStations = saved.map((s) => Station.fromJson(jsonDecode(s))).toList();
    notifyListeners();
  }

  Future<void> saveStationToRecent(Station station) async {
    final prefs = await SharedPreferences.getInstance();
    final List<String> saved = prefs.getStringList('recent_stations') ?? [];
    
    // Remove if already exists to move to top
    saved.removeWhere((s) {
      final decoded = Station.fromJson(jsonDecode(s));
      return decoded.code == station.code;
    });
    
    saved.insert(0, jsonEncode({
      'code': station.code,
      'name': station.name,
      'lat': station.lat,
      'lon': station.lon,
    }));
    
    if (saved.length > 10) saved.removeLast();
    
    await prefs.setStringList('recent_stations', saved);
    recentStations = saved.map((s) => Station.fromJson(jsonDecode(s))).toList();
    notifyListeners();
  }

  void selectStations(Station? src, Station? dst) {
    sourceStation = src;
    destinationStation = dst;
    if (src != null) saveStationToRecent(src);
    if (dst != null) saveStationToRecent(dst);
    notifyListeners();
  }

  Future<void> searchFares() async {
    if (sourceStation == null || destinationStation == null) return;
    isLoading = true;
    error = null;
    quotes = [];
    notifyListeners();

    try {
      final response = await _api.searchFare(sourceStation!.code, destinationStation!.code);
      final searchId = response['searchId'];
      
      if (searchId != null) {
        // Optimized polling: shorter delay, 4 attempts
        for (int i = 0; i < 4; i++) {
          await Future.delayed(const Duration(milliseconds: 800));
          final q = await _api.getQuotes(searchId);
          if (q.isNotEmpty) {
            quotes = q;
            break;
          }
        }
      }
    } catch (e) {
      error = "Search failed";
    }
    
    isLoading = false;
    notifyListeners();
  }

  Future<void> confirmBooking(Quote quote, int quantity) async {
    isLoading = true;
    notifyListeners();
    try {
      currentBooking = await _api.confirmBooking(quote.quoteId, quantity: quantity);
      
      // If status is not CONFIRMED, poll for 5 seconds
      if (currentBooking != null && currentBooking!.status != "CONFIRMED") {
        for (int i = 0; i < 5; i++) {
          await Future.delayed(const Duration(seconds: 1));
          final latest = await _api.getBookingStatus(currentBooking!.bookingId);
          currentBooking = latest;
          if (latest.status == "CONFIRMED") break;
        }
      }
      error = null;
    } catch (e) {
      error = "Booking failed";
    }
    isLoading = false;
    notifyListeners();
  }

  Future<void> refreshStatus() async {
    if (currentBooking == null) return;
    try {
      currentBooking = await _api.refreshBookingStatus(currentBooking!.bookingId);
      notifyListeners();
    } catch (_) {}
  }
}
