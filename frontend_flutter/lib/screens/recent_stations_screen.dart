import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/booking_provider.dart';
import '../theme/brutalist_style.dart';
import '../models/models.dart';

class RecentStationsScreen extends StatelessWidget {
  const RecentStationsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final booking = Provider.of<BookingProvider>(context);

    return Scaffold(
      backgroundColor: AppleColors.bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AppleColors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("Recent Stations", style: AppleStyle.title()),
      ),
      body: booking.recentStations.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.location_off_outlined, size: 64, color: AppleColors.lightGray),
                  const SizedBox(height: 16),
                  Text("No Recent Stations", style: AppleStyle.title().copyWith(color: AppleColors.gray)),
                  const SizedBox(height: 8),
                  Text("Your journeys will appear here", style: AppleStyle.body(color: AppleColors.gray)),
                ],
              ),
            )
          : ListView.separated(
              padding: const EdgeInsets.all(24),
              itemCount: booking.recentStations.length,
              separatorBuilder: (_, __) => const SizedBox(height: 12),
              itemBuilder: (ctx, i) {
                final station = booking.recentStations[i];
                return GestureDetector(
                  onTap: () {
                    // Logic to select this as destination by default or ask
                    booking.selectStations(booking.sourceStation, station);
                    Navigator.pop(context);
                  },
                  child: Container(
                    padding: const EdgeInsets.all(20),
                    decoration: AppleStyle.cardDecoration(),
                    child: Row(
                      children: [
                        const Icon(Icons.history, color: AppleColors.blue, size: 24),
                        const SizedBox(width: 16),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(station.name, style: AppleStyle.body(bold: true)),
                            Text(station.code, style: AppleStyle.footnote()),
                          ],
                        ),
                        const Spacer(),
                        const Icon(Icons.arrow_forward_ios, size: 14, color: AppleColors.lightGray),
                      ],
                    ),
                  ),
                );
              },
            ),
    );
  }
}
