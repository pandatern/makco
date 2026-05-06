import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../providers/booking_provider.dart';
import '../theme/brutalist_style.dart';
import '../widgets/brutalist_button.dart';

class TicketScreen extends StatelessWidget {
  const TicketScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final booking = Provider.of<BookingProvider>(context);
    final status = booking.currentBooking;

    if (status == null) {
      return Scaffold(
        backgroundColor: AppleColors.bg,
        body: Center(child: Text("No active ticket", style: AppleStyle.title())),
      );
    }

    // Fuzzy QR discovery logic preserved from Android, skipping empty strings
    String qrData = status.bookingId;
    if (status.tickets.isNotEmpty) {
      final tkt = status.tickets.first;
      
      final candidates = [
        tkt.qrCodes?.isNotEmpty == true ? tkt.qrCodes!.first : null,
        tkt.qrString,
        tkt.qRCode,
        tkt.qr_code,
        tkt.qr,
        tkt.verificationCode,
        tkt.ticketNumber,
      ];
      
      qrData = candidates.firstWhere(
        (c) => c != null && c.trim().isNotEmpty,
        orElse: () => status.bookingId,
      )!;
    }

    return Scaffold(
      backgroundColor: AppleColors.bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.close, color: AppleColors.black),
          onPressed: () => Navigator.popUntil(context, (route) => route.isFirst),
        ),
        title: Text("Your Ticket", style: AppleStyle.title()),
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              Container(
                decoration: AppleStyle.cardDecoration(),
                padding: const EdgeInsets.all(32),
                child: Column(
                  children: [
                    Text(status.bookingId.substring(0, 8).toUpperCase(), 
                         style: AppleStyle.footnote()),
                    const SizedBox(height: 32),
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: AppleColors.bg, width: 1),
                      ),
                      child: QrImageView(
                        data: qrData,
                        version: QrVersions.auto,
                        size: 240.0,
                        errorCorrectionLevel: QrErrorCorrectLevel.L,
                      ),
                    ),
                    const SizedBox(height: 32),
                    Text("SCAN AT GATE", style: AppleStyle.title().copyWith(letterSpacing: 2)),
                    const SizedBox(height: 8),
                    Text("Increase brightness for faster entry", style: AppleStyle.footnote()),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              Container(
                decoration: AppleStyle.cardDecoration(),
                padding: const EdgeInsets.all(24),
                child: Column(
                  children: [
                    _InfoRow(label: "STATUS", value: status.status, color: AppleColors.blue),
                    const Padding(
                      padding: EdgeInsets.symmetric(vertical: 16),
                      child: Divider(height: 1, color: AppleColors.bg),
                    ),
                    _InfoRow(label: "PASSENGERS", value: "${status.quantity} ADULT"),
                    const SizedBox(height: 12),
                    _InfoRow(label: "TOTAL FARE", value: "₹${status.price.toInt()}"),
                  ],
                ),
              ),
              const SizedBox(height: 32),
              AppleButton(
                text: "Refresh Status",
                color: AppleColors.white,
                onTap: () => booking.refreshStatus(),
              ),
              const SizedBox(height: 80),
            ],
          ),
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;
  final Color? color;
  const _InfoRow({required this.label, required this.value, this.color});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppleStyle.footnote().copyWith(fontWeight: FontWeight.bold)),
        Text(value, style: AppleStyle.body(bold: true, color: color ?? AppleColors.black)),
      ],
    );
  }
}
