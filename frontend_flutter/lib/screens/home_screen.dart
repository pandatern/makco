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
      backgroundColor: BrutalistColors.bg,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 32),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text("MAKCO", style: BrutalistStyle.heading()),
                  GestureDetector(
                    onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ProfileScreen())),
                    child: Container(
                      width: 55,
                      height: 55,
                      decoration: BrutalistStyle.box(color: BrutalistColors.accent),
                      child: const Icon(Icons.person, color: Colors.black, size: 30),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 48),
              Container(
                decoration: BrutalistStyle.box(color: Colors.black),
                padding: const EdgeInsets.all(16),
                child: Text("QUICK BOOKING", style: BrutalistStyle.label().copyWith(color: Colors.white)),
              ),
              const SizedBox(height: 16),
              _StationSelector(
                label: "FROM STATION",
                station: booking.sourceStation,
                onTap: () => _showStationPicker(true),
              ),
              const SizedBox(height: 16),
              _StationSelector(
                label: "TO STATION",
                station: booking.destinationStation,
                onTap: () => _showStationPicker(false),
              ),
              const SizedBox(height: 32),
              BrutalistButton(
                text: "Find Best Fare",
                onTap: (booking.sourceStation != null && booking.destinationStation != null)
                    ? () {
                        booking.searchFares();
                        Navigator.push(context, MaterialPageRoute(builder: (_) => const BookingScreen()));
                      }
                    : null,
              ),
              const SizedBox(height: 48),
              Row(
                children: [
                  Expanded(
                    child: _MenuAction(
                      icon: Icons.confirmation_number,
                      label: "Tickets",
                      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryScreen())),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: _MenuAction(
                      icon: Icons.map,
                      label: "Route Map",
                      onTap: () {},
                    ),
                  ),
                ],
              ),
              const Spacer(),
              Center(
                child: Text("v2.0 • PREMIUM BRUTALIST", style: BrutalistStyle.label().copyWith(color: Colors.grey, fontSize: 10)),
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
        padding: const EdgeInsets.all(24),
        decoration: BrutalistStyle.box(
          color: station != null ? BrutalistColors.white : Colors.white.withOpacity(0.6),
        ),
        child: Row(
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(label, style: BrutalistStyle.label().copyWith(fontSize: 10, color: Colors.grey[600])),
                const SizedBox(height: 4),
                Text(
                  station?.name ?? "TAP TO SELECT",
                  style: BrutalistStyle.title().copyWith(fontSize: 18),
                ),
              ],
            ),
            const Spacer(),
            const Icon(Icons.keyboard_arrow_down, size: 28),
          ],
        ),
      ),
    );
  }
}

class _MenuAction extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _MenuAction({required this.icon, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BrutalistStyle.box(),
        child: Column(
          children: [
            Icon(icon, size: 32),
            const SizedBox(height: 12),
            Text(label.toUpperCase(), style: BrutalistStyle.label()),
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
      height: MediaQuery.of(context).size.height * 0.85,
      decoration: const BoxDecoration(
        color: BrutalistColors.bg,
        border: Border(top: BorderSide(color: Colors.black, width: 6)),
      ),
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text("CHOOSE STATION", style: BrutalistStyle.heading().copyWith(fontSize: 32)),
          const SizedBox(height: 24),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 4),
            decoration: BrutalistStyle.box(hasShadow: false),
            child: TextField(
              onChanged: (v) => setState(() => query = v),
              style: BrutalistStyle.body(bold: true),
              autofocus: true,
              decoration: const InputDecoration(
                hintText: "SEARCH...",
                border: InputBorder.none,
                icon: Icon(Icons.search, color: Colors.black, size: 28),
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
                  padding: const EdgeInsets.only(bottom: 16),
                  child: GestureDetector(
                    onTap: () => widget.onSelected(s),
                    child: Container(
                      padding: const EdgeInsets.all(20),
                      decoration: BrutalistStyle.box(hasShadow: false),
                      child: Text(s.name, style: BrutalistStyle.title().copyWith(fontSize: 18)),
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
