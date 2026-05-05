import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/booking_provider.dart';
import '../theme/brutalist_style.dart';
import '../widgets/brutalist_button.dart';
import '../models/models.dart';
import 'ticket_screen.dart';
import 'payment_webview.dart';

class BookingScreen extends StatefulWidget {
  const BookingScreen({Key? key}) : super(key: key);

  @override
  _BookingScreenState createState() => _BookingScreenState();
}

class _BookingScreenState extends State<BookingScreen> {
  int _quantity = 1;
  Quote? _selectedQuote;

  void _handlePayment() async {
    final booking = Provider.of<BookingProvider>(context, listen: false);
    await booking.confirmBooking(_selectedQuote!, _quantity);
    
    final status = booking.currentBooking;
    if (status != null) {
      final paymentUrl = status.payment?['paymentOrder']?['payment_links']?['web'];
      
      if (paymentUrl != null) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => PaymentWebView(
              paymentUrl: paymentUrl,
              onSuccess: () async {
                await booking.refreshStatus();
                Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const TicketScreen()));
              },
              onCancel: () {
                Navigator.pop(context); // close webview
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text("Payment Cancelled", style: BrutalistStyle.body(color: Colors.white)), backgroundColor: BrutalistColors.error)
                );
              },
            ),
          ),
        );
      } else {
        // No payment required or already paid
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const TicketScreen()));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final booking = Provider.of<BookingProvider>(context);

    return Scaffold(
      backgroundColor: BrutalistColors.white,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("CHOOSE TICKET", style: BrutalistStyle.title()),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BrutalistStyle.containerDecoration(color: BrutalistColors.gray),
              child: Column(
                children: [
                  _StationLeg(label: "FROM", name: booking.sourceStation?.name ?? "..."),
                  const Padding(
                    padding: EdgeInsets.symmetric(vertical: 8),
                    child: Divider(color: Colors.black, thickness: 2),
                  ),
                  _StationLeg(label: "TO", name: booking.destinationStation?.name ?? "..."),
                ],
              ),
            ),
            const SizedBox(height: 32),
            Text("AVAILABLE OPTIONS", style: BrutalistStyle.label()),
            const SizedBox(height: 16),
            if (booking.isLoading && booking.quotes.isEmpty)
              const Center(child: CircularProgressIndicator())
            else if (booking.quotes.isEmpty)
              Text("No fares found.", style: BrutalistStyle.body())
            else
              ...booking.quotes.map((q) => Padding(
                padding: const EdgeInsets.only(bottom: 12),
                child: GestureDetector(
                  onTap: () => setState(() => _selectedQuote = q),
                  child: Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BrutalistStyle.containerDecoration(
                      color: _selectedQuote?.quoteId == q.quoteId ? BrutalistColors.accent : BrutalistColors.white,
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(q.type == "SingleJourney" ? "SINGLE" : "RETURN", style: BrutalistStyle.title().copyWith(fontSize: 16)),
                            Text("Valid for today", style: BrutalistStyle.label(color: Colors.grey)),
                          ],
                        ),
                        Text("₹${q.price.toInt()}", style: BrutalistStyle.heading().copyWith(fontSize: 24)),
                      ],
                    ),
                  ),
                ),
              )).toList(),
            const Spacer(),
            if (_selectedQuote != null) ...[
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text("QUANTITY", style: BrutalistStyle.title()),
                  Container(
                    decoration: BrutalistStyle.containerDecoration(radius: 12, hasShadow: false),
                    child: Row(
                      children: [
                        IconButton(onPressed: _quantity > 1 ? () => setState(() => _quantity--) : null, icon: const Icon(Icons.remove)),
                        Text("$_quantity", style: BrutalistStyle.title()),
                        IconButton(onPressed: _quantity < 5 ? () => setState(() => _quantity++) : null, icon: const Icon(Icons.add)),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              BrutalistButton(
                text: "Pay ₹${(_selectedQuote!.price * _quantity).toInt()}",
                isLoading: booking.isLoading,
                onTap: _handlePayment,
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _StationLeg extends StatelessWidget {
  final String label;
  final String name;
  const _StationLeg({required this.label, required this.name});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        SizedBox(width: 50, child: Text(label, style: BrutalistStyle.label(color: Colors.grey))),
        const SizedBox(width: 16),
        Expanded(child: Text(name, style: BrutalistStyle.title().copyWith(fontSize: 18), overflow: TextOverflow.ellipsis)),
      ],
    );
  }
}
