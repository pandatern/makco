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
      backgroundColor: BrutalistColors.gray,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 32),
              Row(
                mainAxisAlignment: Arrangement.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("MAKCO v2.0", style: BrutalistStyle.heading().copyWith(fontSize: 40)),
                      Text("METRO TICKETING", style: BrutalistStyle.label(color: BrutalistColors.primary)),
                    ],
                  ),
                  GestureDetector(
                    onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ProfileScreen())),
                    child: Container(
                      width: 50,
                      height: 50,
                      decoration: BrutalistStyle.containerDecoration(radius: 25),
                      child: const Icon(Icons.person, color: Colors.black),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 48),
              _StationSelector(
                label: "FROM",
                station: booking.sourceStation,
                onTap: () => _showStationPicker(true),
              ),
              const SizedBox(height: 16),
              _StationSelector(
                label: "TO",
                station: booking.destinationStation,
                onTap: () => _showStationPicker(false),
              ),
              const SizedBox(height: 32),
              BrutalistButton(
                text: "Search Fares",
                onTap: (booking.sourceStation != null && booking.destinationStation != null)
                    ? () {
                        booking.searchFares();
                        Navigator.push(context, MaterialPageRoute(builder: (_) => const BookingScreen()));
                      }
                    : null,
              ),
              const SizedBox(height: 48),
              Text("QUICK ACTIONS", style: BrutalistStyle.title()),
              const SizedBox(height: 16),
              Row(
                children: [
                  _QuickAction(icon: Icons.history, label: "History", onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryScreen()));
                  }),
                  const SizedBox(width: 16),
                  _QuickAction(icon: Icons.settings, label: "Profile", onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (_) => const ProfileScreen()));
                  }),
                ],
              ),
              const Spacer(),
              Center(
                child: Text("${booking.stations.length} STATIONS LOADED", style: BrutalistStyle.label(color: Colors.grey)),
              ),
              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }
}

class _StationSelector extends StatelessWidget {
  final String label;
  final Station? station;
  final VoidCallback onTap;

  const _StationSelector({required this.label, this.station, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BrutalistStyle.containerDecoration(
          color: station != null ? BrutalistColors.white : BrutalistColors.white.withOpacity(0.5),
        ),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: BrutalistColors.black,
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(Icons.location_on, color: Colors.white, size: 20),
            ),
            const SizedBox(width: 16),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(label, style: BrutalistStyle.label(color: Colors.grey)),
                const SizedBox(height: 4),
                Text(
                  station?.name ?? "Select Station",
                  style: BrutalistStyle.title().copyWith(fontSize: 18),
                ),
              ],
            ),
            const Spacer(),
            const Icon(Icons.arrow_forward_ios, size: 16),
          ],
        ),
      ),
    );
  }
}

class _QuickAction extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _QuickAction({required this.icon, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16),
          decoration: BrutalistStyle.containerDecoration(),
          child: Column(
            children: [
              Icon(icon, size: 24),
              const SizedBox(height: 8),
              Text(label.toUpperCase(), style: BrutalistStyle.label()),
            ],
          ),
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
      height: MediaQuery.of(context).size.height * 0.8,
      decoration: const BoxDecoration(
        color: BrutalistColors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(32)),
        border: Border(top: BorderSide(color: Colors.black, width: 4)),
      ),
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text("PICK STATION", style: BrutalistStyle.heading()),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            decoration: BrutalistStyle.containerDecoration(hasShadow: false),
            child: TextField(
              onChanged: (v) => setState(() => query = v),
              decoration: const InputDecoration(
                hintText: "Search stations...",
                border: InputBorder.none,
                icon: Icon(Icons.search, color: Colors.black),
              ),
            ),
          ),
          const SizedBox(height: 24),
          Expanded(
            child: ListView.builder(
              itemCount: filtered.length,
              itemBuilder: (ctx, i) {
                final s = filtered[i];
                return Padding(
                  padding: const EdgeInsets.only(bottom: 12),
                  child: GestureDetector(
                    onTap: () => widget.onSelected(s),
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BrutalistStyle.containerDecoration(hasShadow: false, radius: 12),
                      child: Text(s.name, style: BrutalistStyle.title().copyWith(fontSize: 16)),
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
