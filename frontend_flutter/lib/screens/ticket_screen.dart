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
      return Scaffold(body: Center(child: Text("No active ticket", style: BrutalistStyle.title())));
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
      backgroundColor: BrutalistColors.primary,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.close, color: Colors.white),
          onPressed: () => Navigator.popUntil(context, (route) => route.isFirst),
        ),
        title: Text("YOUR TICKET", style: BrutalistStyle.title(color: Colors.white)),
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              Container(
                decoration: BrutalistStyle.containerDecoration(),
                padding: const EdgeInsets.all(24),
                child: Column(
                  children: [
                    Text(status.bookingId.substring(0, 8).toUpperCase(), 
                         style: BrutalistStyle.label(color: Colors.grey)),
                    const SizedBox(height: 24),
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        border: Border.all(color: Colors.black, width: 2),
                      ),
                      child: QrImageView(
                        data: qrData,
                        version: QrVersions.auto,
                        size: 250.0,
                        errorCorrectionLevel: QrErrorCorrectLevel.L,
                      ),
                    ),
                    const SizedBox(height: 24),
                    Text("SCAN AT GATE", style: BrutalistStyle.title()),
                    const SizedBox(height: 8),
                    Text("Keep brightness high for scanning", style: BrutalistStyle.label(color: Colors.grey)),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              Container(
                decoration: BrutalistStyle.containerDecoration(color: BrutalistColors.white),
                padding: const EdgeInsets.all(20),
                child: Column(
                  children: [
                    _InfoRow(label: "STATUS", value: status.status, color: BrutalistColors.primary),
                    const Divider(height: 32, thickness: 2, color: Colors.black),
                    _InfoRow(label: "PASSENGERS", value: "${status.quantity} ADULT"),
                    const SizedBox(height: 16),
                    _InfoRow(label: "AMOUNT", value: "₹${status.price.toInt()}"),
                  ],
                ),
              ),
              const SizedBox(height: 32),
              BrutalistButton(
                text: "Refresh Status",
                color: BrutalistColors.white,
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
        Text(label, style: BrutalistStyle.label(color: Colors.grey)),
        Text(value, style: BrutalistStyle.title(color: color ?? Colors.black).copyWith(fontSize: 16)),
      ],
    );
  }
}
