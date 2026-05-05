import 'package:flutter/material.dart';
import '../models/models.dart';
import '../services/api_service.dart';

class BookingProvider extends ChangeNotifier {
  final ApiService _api;
  
  List<Station> stations = [];
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
      error = null;
    } catch (e) {
      error = "Failed to load stations";
    }
    isLoading = false;
    notifyListeners();
  }

  void selectStations(Station? src, Station? dst) {
    sourceStation = src;
    destinationStation = dst;
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
        // Simple polling logic: 3 attempts
        for (int i = 0; i < 3; i++) {
          await Future.delayed(const Duration(seconds: 2));
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
