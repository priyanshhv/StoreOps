package com.laundry.version_one.booking;

import org.springframework.stereotype.Service;

@Service
public class BookingMapper {
    public Booking toBooking(BookingRequest request){
        Booking booking = new Booking();
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());
        return booking;
    }

    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserName(booking.getUser().getFullName());
        response.setMachineId(booking.getMachine().getId());
        response.setCreatedDate(booking.getCreatedDate());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setBill(booking.getTotalAmount());
        return response;
    }
}
