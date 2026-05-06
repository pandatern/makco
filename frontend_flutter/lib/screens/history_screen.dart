import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../models/models.dart';
import '../theme/brutalist_style.dart';
import 'ticket_screen.dart';

class HistoryScreen extends StatefulWidget {
  const HistoryScreen({Key? key}) : super(key: key);

  @override
  _HistoryScreenState createState() => _HistoryScreenState();
}

class _HistoryScreenState extends State<HistoryScreen> {
  List<BookingStatus>? _tickets;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadTickets();
  }

  Future<void> _loadTickets() async {
    setState(() { _isLoading = true; _error = null; });
    try {
      final auth = Provider.of<AuthProvider>(context, listen: false);
      final tickets = await auth.apiService.getTicketBookings();
      setState(() => _tickets = tickets);
    } catch (e) {
      setState(() => _error = "Failed to load history");
    }
    setState(() => _isLoading = false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppleColors.bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("My Tickets", style: AppleStyle.title()),
      ),
      body: _isLoading 
        ? const Center(child: CircularProgressIndicator(color: AppleColors.blue))
        : _error != null 
          ? Center(child: Text(_error!, style: AppleStyle.body(color: AppleColors.error)))
          : _tickets == null || _tickets!.isEmpty
            ? Center(child: Text("No tickets found.", style: AppleStyle.body()))
            : ListView.builder(
                padding: const EdgeInsets.all(24),
                itemCount: _tickets!.length,
                itemBuilder: (ctx, i) {
                  final ticket = _tickets![i];
                  return Padding(
                    padding: const EdgeInsets.only(bottom: 16),
                    child: Container(
                      padding: const EdgeInsets.all(20),
                      decoration: AppleStyle.cardDecoration(),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(ticket.bookingId.substring(0,8).toUpperCase(), style: AppleStyle.title().copyWith(fontSize: 18)),
                              const SizedBox(height: 8),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                decoration: BoxDecoration(color: AppleColors.blue, borderRadius: BorderRadius.circular(4)),
                                child: Text(ticket.status, style: AppleStyle.footnote().copyWith(color: Colors.white, fontWeight: FontWeight.bold)),
                              ),
                            ],
                          ),
                          Text("₹${ticket.price.toInt()}", style: AppleStyle.title().copyWith(fontSize: 24, color: AppleColors.black)),
                        ],
                      ),
                    ),
                  );
                },
              ),
    );
  }
}
