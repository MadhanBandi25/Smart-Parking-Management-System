package com.smartparking.management.service;

import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.dto.response.QrCodeResponse;

public interface QrCodeService {

    QrCodeResponse generateQrCode(String bookingNumber);
    BookingResponse verifyQrCode(String bookingNumber);

}
