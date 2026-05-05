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
      backgroundColor: BrutalistColors.gray,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("MY TICKETS", style: BrutalistStyle.title()),
      ),
      body: _isLoading 
        ? const Center(child: CircularProgressIndicator(color: BrutalistColors.black))
        : _error != null 
          ? Center(child: Text(_error!, style: BrutalistStyle.body(color: BrutalistColors.error)))
          : _tickets == null || _tickets!.isEmpty
            ? Center(child: Text("No tickets found.", style: BrutalistStyle.body()))
            : ListView.builder(
                padding: const EdgeInsets.all(24),
                itemCount: _tickets!.length,
                itemBuilder: (ctx, i) {
                  final ticket = _tickets![i];
                  return Padding(
                    padding: const EdgeInsets.only(bottom: 16),
                    child: Container(
                      padding: const EdgeInsets.all(20),
                      decoration: BrutalistStyle.containerDecoration(),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(ticket.bookingId.substring(0,8).toUpperCase(), style: BrutalistStyle.title().copyWith(fontSize: 18)),
                              const SizedBox(height: 8),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                decoration: BoxDecoration(color: BrutalistColors.primary, borderRadius: BorderRadius.circular(4)),
                                child: Text(ticket.status, style: BrutalistStyle.label(color: Colors.white)),
                              ),
                            ],
                          ),
                          Text("₹${ticket.price.toInt()}", style: BrutalistStyle.heading().copyWith(fontSize: 24)),
                        ],
                      ),
                    ),
                  );
                },
              ),
    );
  }
}
