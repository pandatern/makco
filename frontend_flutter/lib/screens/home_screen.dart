import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../providers/booking_provider.dart';
import '../theme/brutalist_style.dart';
import '../widgets/brutalist_button.dart';
import '../models/models.dart';
import 'booking_screen.dart';
import 'history_screen.dart';
import 'profile_screen.dart';
import 'recent_stations_screen.dart';
import 'settings_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() => 
      Provider.of<BookingProvider>(context, listen: false).fetchStations()
    );
  }

  void _showStationPicker(bool isSource) {
    final booking = Provider.of<BookingProvider>(context, listen: false);
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => StationPicker(
        stations: booking.stations,
        onSelected: (station) {
          if (isSource) {
            booking.selectStations(station, booking.destinationStation);
          } else {
            booking.selectStations(booking.sourceStation, station);
          }
          Navigator.pop(ctx);
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final booking = Provider.of<BookingProvider>(context);

    return Scaffold(
      backgroundColor: AppleColors.bg,
      body: SafeArea(
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("Makco", style: AppleStyle.largeTitle()),
                    GestureDetector(
                      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const SettingsScreen())),
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: const BoxDecoration(
                          color: AppleColors.white,
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(Icons.settings_outlined, color: AppleColors.black, size: 24),
                      ),
                    ),
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("WHERE TO?", style: AppleStyle.footnote().copyWith(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 12),
                    Container(
                      decoration: AppleStyle.cardDecoration(hasShadow: true),
                      child: Column(
                        children: [
                          _StationRow(
                            icon: Icons.circle_outlined,
                            label: "From",
                            value: booking.sourceStation?.name ?? "Select Source",
                            onTap: () => _showStationPicker(true),
                            showDivider: true,
                          ),
                          _StationRow(
                            icon: Icons.location_on,
                            iconColor: AppleColors.blue,
                            label: "To",
                            value: booking.destinationStation?.name ?? "Select Destination",
                            onTap: () => _showStationPicker(false),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 24),
                    if (booking.recentStations.isNotEmpty) ...[
                      SizedBox(
                        height: 40,
                        child: ListView.separated(
                          scrollDirection: Axis.horizontal,
                          itemCount: booking.recentStations.length,
                          separatorBuilder: (_, __) => const SizedBox(width: 8),
                          itemBuilder: (ctx, i) {
                            final s = booking.recentStations[i];
                            return GestureDetector(
                              onTap: () => booking.selectStations(booking.sourceStation, s),
                              child: Container(
                                padding: const EdgeInsets.symmetric(horizontal: 16),
                                decoration: BoxDecoration(
                                  color: AppleColors.white,
                                  borderRadius: BorderRadius.circular(20),
                                  border: Border.all(color: AppleColors.lightGray.withOpacity(0.5)),
                                ),
                                alignment: Alignment.center,
                                child: Text(s.name, style: AppleStyle.footnote().copyWith(color: AppleColors.black, fontWeight: FontWeight.bold)),
                              ),
                            );
                          },
                        ),
                      ),
                      const SizedBox(height: 24),
                    ],
                    BrutalistButton(
                      text: "Find Fares",
                      onTap: (booking.sourceStation != null && booking.destinationStation != null)
                          ? () {
                              booking.searchFares();
                              Navigator.push(context, MaterialPageRoute(builder: (_) => const BookingScreen()));
                            }
                          : null,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 40),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("MY TRAVEL", style: AppleStyle.footnote().copyWith(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: _AppleActionCard(
                            icon: Icons.receipt_long_outlined,
                            label: "Tickets",
                            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryScreen())),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: _AppleActionCard(
                            icon: Icons.history_outlined,
                            label: "Recents",
                            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const RecentStationsScreen())),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 40),
              Center(
                child: Padding(
                  padding: const EdgeInsets.only(bottom: 24),
                  child: Text("v2.1.0 • Designed for Chennai", style: AppleStyle.footnote()),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _StationRow extends StatelessWidget {
  final IconData icon;
  final Color iconColor;
  final String label;
  final String value;
  final VoidCallback onTap;
  final bool showDivider;

  const _StationRow({
    required this.icon,
    this.iconColor = Colors.grey,
    required this.label,
    required this.value,
    required this.onTap,
    this.showDivider = false,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
        child: Column(
          children: [
            Row(
              children: [
                Icon(icon, color: iconColor, size: 24),
                const SizedBox(width: 16),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(label, style: AppleStyle.footnote()),
                    const SizedBox(height: 4),
                    Text(value, style: AppleStyle.body(bold: true)),
                  ],
                ),
                const Spacer(),
                const Icon(Icons.chevron_right, color: AppleColors.lightGray),
              ],
            ),
            if (showDivider) 
              const Padding(
                padding: EdgeInsets.only(top: 20, left: 40),
                child: Divider(height: 1, color: AppleColors.bg),
              ),
          ],
        ),
      ),
    );
  }
}

class _AppleActionCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _AppleActionCard({required this.icon, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: AppleStyle.cardDecoration(hasShadow: true),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: AppleColors.blue, size: 32),
            const SizedBox(height: 16),
            Text(label, style: AppleStyle.body(bold: true)),
          ],
        ),
      ),
    );
  }
}

class StationPicker extends StatefulWidget {
  final List<Station> stations;
  final Function(Station) onSelected;

  const StationPicker({Key? key, required this.stations, required this.onSelected}) : super(key: key);

  @override
  _StationPickerState createState() => _StationPickerState();
}

class _StationPickerState extends State<StationPicker> {
  String query = "";
  
  @override
  Widget build(BuildContext context) {
    final filtered = widget.stations.where((s) => s.name.toLowerCase().contains(query.toLowerCase())).toList();

    return Container(
      height: MediaQuery.of(context).size.height * 0.9,
      decoration: const BoxDecoration(
        color: AppleColors.bg,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      child: Column(
        children: [
          const SizedBox(height: 12),
          Container(width: 40, height: 6, decoration: BoxDecoration(color: AppleColors.lightGray, borderRadius: BorderRadius.circular(3))),
          Padding(
            padding: const EdgeInsets.all(24),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("Select Station", style: AppleStyle.title()),
                GestureDetector(
                  onTap: () => Navigator.pop(context),
                  child: Container(
                    padding: const EdgeInsets.all(4),
                    decoration: const BoxDecoration(color: AppleColors.lightGray, shape: BoxShape.circle),
                    child: const Icon(Icons.close, size: 20, color: Colors.white),
                  ),
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              decoration: BoxDecoration(color: AppleColors.white, borderRadius: BorderRadius.circular(12)),
              child: TextField(
                onChanged: (v) => setState(() => query = v),
                autofocus: true,
                decoration: const InputDecoration(
                  hintText: "Search stations",
                  border: InputBorder.none,
                  icon: Icon(Icons.search, color: AppleColors.gray),
                ),
              ),
            ),
          ),
          const SizedBox(height: 24),
          Expanded(
            child: ListView.separated(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              itemCount: filtered.length,
              separatorBuilder: (_, __) => const Divider(height: 1, color: AppleColors.bg),
              itemBuilder: (ctx, i) {
                final s = filtered[i];
                return ListTile(
                  contentPadding: const EdgeInsets.symmetric(vertical: 8),
                  title: Text(s.name, style: AppleStyle.body()),
                  trailing: const Icon(Icons.chevron_right, color: AppleColors.lightGray),
                  onTap: () => widget.onSelected(s),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
