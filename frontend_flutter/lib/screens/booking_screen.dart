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
      if (status.status == "CONFIRMED") {
        // Skip straight to ticket
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const TicketScreen()));
        return;
      }

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
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("Payment Cancelled"), backgroundColor: AppleColors.error)
                );
              },
            ),
          ),
        );
      } else {
        // Status is PENDING but no payment URL - this is an error for normal users
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("Payment session not available. Status: ${status.status}"),
            backgroundColor: AppleColors.error,
          )
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final booking = Provider.of<BookingProvider>(context);

    return Scaffold(
      backgroundColor: AppleColors.bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("Choose Ticket", style: AppleStyle.title()),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(20),
              decoration: AppleStyle.cardDecoration(),
              child: Column(
                children: [
                  _StationLeg(icon: Icons.circle_outlined, name: booking.sourceStation?.name ?? "..."),
                  const Padding(
                    padding: EdgeInsets.only(left: 40, top: 8, bottom: 8),
                    child: Divider(color: AppleColors.bg, thickness: 1),
                  ),
                  _StationLeg(icon: Icons.location_on, iconColor: AppleColors.blue, name: booking.destinationStation?.name ?? "..."),
                ],
              ),
            ),
            const SizedBox(height: 32),
            Text("AVAILABLE FARES", style: AppleStyle.footnote().copyWith(fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            if (booking.isLoading && booking.quotes.isEmpty)
              const Center(child: CircularProgressIndicator(color: AppleColors.blue))
            else if (booking.quotes.isEmpty)
              Padding(
                padding: const EdgeInsets.all(20),
                child: Text("No fares found for this route.", style: AppleStyle.body(color: AppleColors.gray)),
              )
            else
              ...booking.quotes.map((q) => Padding(
                padding: const EdgeInsets.only(bottom: 12),
                child: GestureDetector(
                  onTap: () => setState(() => _selectedQuote = q),
                  child: AnimatedContainer(
                    duration: const Duration(milliseconds: 200),
                    padding: const EdgeInsets.all(20),
                    decoration: AppleStyle.cardDecoration(
                      color: _selectedQuote?.quoteId == q.quoteId ? AppleColors.blue : AppleColors.white,
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              q.type == "SingleJourney" ? "Single Journey" : "Return Trip", 
                              style: AppleStyle.body(bold: true, color: _selectedQuote?.quoteId == q.quoteId ? Colors.white : AppleColors.black)
                            ),
                            Text(
                              "Valid for today only", 
                              style: AppleStyle.footnote().copyWith(color: _selectedQuote?.quoteId == q.quoteId ? Colors.white.withOpacity(0.8) : AppleColors.gray)
                            ),
                          ],
                        ),
                        Text(
                          "₹${q.price.toInt()}", 
                          style: AppleStyle.title().copyWith(
                            fontSize: 24, 
                            color: _selectedQuote?.quoteId == q.quoteId ? Colors.white : AppleColors.black
                          )
                        ),
                      ],
                    ),
                  ),
                ),
              )).toList(),
            const Spacer(),
            if (_selectedQuote != null) ...[
              Container(
                padding: const EdgeInsets.all(20),
                decoration: AppleStyle.cardDecoration(),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("Number of Tickets", style: AppleStyle.body(bold: true)),
                    Row(
                      children: [
                        _QtyBtn(icon: Icons.remove, onTap: _quantity > 1 ? () => setState(() => _quantity--) : null),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          child: Text("$_quantity", style: AppleStyle.title()),
                        ),
                        _QtyBtn(icon: Icons.add, onTap: _quantity < 5 ? () => setState(() => _quantity++) : null),
                      ],
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              AppleButton(
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
  final IconData icon;
  final Color iconColor;
  final String name;
  const _StationLeg({required this.icon, this.iconColor = Colors.grey, required this.name});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, color: iconColor, size: 20),
        const SizedBox(width: 20),
        Expanded(child: Text(name, style: AppleStyle.body(bold: true), overflow: TextOverflow.ellipsis)),
      ],
    );
  }
}

class _QtyBtn extends StatelessWidget {
  final IconData icon;
  final VoidCallback? onTap;
  const _QtyBtn({required this.icon, this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: onTap == null ? AppleColors.bg : AppleColors.blue.withOpacity(0.1),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(icon, size: 20, color: onTap == null ? AppleColors.lightGray : AppleColors.blue),
      ),
    );
  }
}
