class Station {
  final String code;
  final String name;
  final double lat;
  final double lon;
  final String? stationType;

  Station({
    required this.code,
    required this.name,
    required this.lat,
    required this.lon,
    this.stationType,
  });

  factory Station.fromJson(Map<String, dynamic> json) {
    return Station(
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      lat: (json['lat'] ?? 0).toDouble(),
      lon: (json['lon'] ?? 0).toDouble(),
      stationType: json['stationType'],
    );
  }
}

class Quote {
  final String quoteId;
  final double price;
  final String? type;
  final List<Station>? stations;

  Quote({
    required this.quoteId,
    required this.price,
    this.type,
    this.stations,
  });

  factory Quote.fromJson(Map<String, dynamic> json) {
    return Quote(
      quoteId: json['quoteId'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      type: json['_type'],
      stations: json['stations'] != null
          ? (json['stations'] as List).map((i) => Station.fromJson(i)).toList()
          : null,
    );
  }
}

class Ticket {
  final String id;
  final String? qrString;
  final List<String>? qrCodes;
  final String? qRCode;
  final String? qr_code;
  final String? qr;
  final String? verificationCode;
  final String? ticketNumber;
  final String status;

  Ticket({
    required this.id,
    this.qrString,
    this.qrCodes,
    this.qRCode,
    this.qr_code,
    this.qr,
    this.verificationCode,
    this.ticketNumber,
    required this.status,
  });

  factory Ticket.fromJson(Map<String, dynamic> json) {
    return Ticket(
      id: json['id'] ?? '',
      qrString: json['qrString'],
      qrCodes: json['qrCodes'] != null ? List<String>.from(json['qrCodes']) : null,
      qRCode: json['qRCode'],
      qr_code: json['qr_code'],
      qr: json['qr'],
      verificationCode: json['verificationCode'],
      ticketNumber: json['ticketNumber'],
      status: json['status'] ?? 'ACTIVE',
    );
  }
}

class BookingStatus {
  final String bookingId;
  final String status;
  final double price;
  final List<Station>? stations;
  final String? validTill;
  final List<Ticket> tickets;
  final int quantity;
  final String? vehicleType;
  final Map<String, dynamic>? payment;
  final bool isAdmin;

  BookingStatus({
    required this.bookingId,
    required this.status,
    required this.price,
    this.stations,
    this.validTill,
    required this.tickets,
    required this.quantity,
    this.vehicleType,
    this.payment,
    this.isAdmin = false,
  });

  factory BookingStatus.fromJson(Map<String, dynamic> json) {
    return BookingStatus(
      bookingId: json['bookingId'] ?? '',
      status: json['status'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      stations: json['stations'] != null
          ? (json['stations'] as List).map((i) => Station.fromJson(i)).toList()
          : null,
      validTill: json['validTill'],
      tickets: json['tickets'] != null
          ? (json['tickets'] as List).map((i) => Ticket.fromJson(i)).toList()
          : [],
      quantity: json['quantity'] ?? 1,
      vehicleType: json['vehicleType'],
      payment: json['payment'],
      isAdmin: json['isAdmin'] ?? false,
    );
  }
}
