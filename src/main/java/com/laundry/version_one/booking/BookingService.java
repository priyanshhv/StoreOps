package com.laundry.version_one.booking;


import com.laundry.version_one.common.PageResponse;
import com.laundry.version_one.exception.OperationNotPermittedException;
import com.laundry.version_one.machine.Machine;
import com.laundry.version_one.machine.MachineRepository;
import com.laundry.version_one.store.Store;
import com.laundry.version_one.store.StoreRepository;
import com.laundry.version_one.user.User;
import com.laundry.version_one.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BookingMapper bookingMapper;


    private final Long time1 = 1L;
    private final Long time2 = 2L;
    private final Long time3 = 1L;
    private final Long time4 = 1L;

//    private final Long time1 = 6L;
//    private final Long time2 = 10L;
//    private final Long time3 = 5L;
//    private final Long time4 = 3L;

    public List<BookingSlotResponse> findAvailableSlots(Integer machineId) {
        List<BookingSlotResponse> availableSlots = new ArrayList<>();
        if(machineRepository.findById(machineId).isEmpty()){
            throw new EntityNotFoundException("Machine id not found");
        }
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Booking> bookings = bookingRepository.findAllBookingsHavingMachineIdCreatedTodayAfterCurrentTime(machineId,currentDate,currentTime);
        //now I want to find the free time slots like I will iterate and for the ith i will insert the newly created
        //BookingSlotResponse like BookingSlotResponse.startTime as the endtime + 5min of the ith slot and BookingSlotResponse.endTime is the start time of the i+1 th slot - 5min
        //if and only if the difference between the ith booking's endtime and the i+1 th bookings start time is 15 minutes
        //then push the BookingSlotResponse in the availableSlots complete the functionality

        if(bookings.isEmpty()){
            availableSlots.add(new BookingSlotResponse(currentTime.plusMinutes(time1),LocalTime.MAX.minusMinutes(time2)));
            return availableSlots;
        }

        // Iterate through the sorted bookings
        for (int i = 0; i < bookings.size(); i++) {
//            System.out.println(" "+bookings.get(i).getStartTime() + " ----- " + bookings.get(i).getEndTime()+"\n");
            Booking currentBooking = bookings.get(i);
            LocalTime currentEndTime = currentBooking.getEndTime();

            // Calculate the start time of the next slot (end time of current booking + 5 minutes)
            LocalTime nextSlotStartTime = currentEndTime.plusMinutes(time1);

            if (i < bookings.size() - 1) {
                // Get the start time of the next booking
                LocalTime nextBookingStartTime = bookings.get(i + 1).getStartTime();

                // Calculate the end time of the current slot (start time of next booking - 10 minutes)
                LocalTime currentSlotEndTime = nextBookingStartTime.minusMinutes(time1);

                // Check if the slot duration is at least 10 minutes
                if (currentSlotEndTime.isAfter(nextSlotStartTime.plusMinutes(time2))) {
                    availableSlots.add(new BookingSlotResponse(nextSlotStartTime, currentSlotEndTime));
                }
            } else {
                // Last booking: Calculate the end time of the last slot (midnight - 10 minutes)
                LocalTime lastSlotEndTime = LocalTime.MAX.minusMinutes(time2);
                if (lastSlotEndTime.isAfter(nextSlotStartTime)) {
                    availableSlots.add(new BookingSlotResponse(nextSlotStartTime, lastSlotEndTime));
                }
            }
        }

        return availableSlots;
    }

    public void updateUserBalanceAndStoreRevenue(Integer bookingId, Integer storeId, Integer userId){
        System.out.println("Updating the store("+storeId+") revenue and user("+userId+") balance");
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<Store> store = storeRepository.findById(storeId);
        Optional<User> user = userRepository.findById(userId);
        if(booking.isEmpty() || store.isEmpty() || user.isEmpty()){
            throw new EntityNotFoundException("Either Booking, Store and User have been deleted. Entity not found!");
        }
        Double amount = booking.get().getTotalAmount();
        if(user.get().getBalance()==null){
            user.get().setBalance(-amount);
        }
        else{
            Double userBalance = user.get().getBalance();
            user.get().setBalance(userBalance-amount);
        }
        Double storeRevenue = store.get().getStoreRevenue();
        store.get().setStoreRevenue(storeRevenue+amount);
        storeRepository.save(store.get());
        userRepository.save(user.get());

        System.out.println("\n\n Transaction to update the balance is stoped now \n\n");

        return;
    }

    //store owners can't book a machine
    //you can book a machine for today only future bookings are not permitted
    public Integer save(Integer machineId, BookingRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        boolean hasOwnerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("OWNER"));
        if(hasOwnerRole)
        {
            throw new OperationNotPermittedException("Owners can't book a machine");
        }

        if(machineRepository.findById(machineId).isEmpty()){
            throw new EntityNotFoundException("Machine id not found");
        }

        LocalDate currentDate = LocalDate.now();
        LocalTime lastSlotEndTime = LocalTime.MAX.minusMinutes(time2);
        if (request.endTime().isAfter(lastSlotEndTime) || request.startTime().isAfter(request.endTime().minusMinutes(time2)) || request.startTime().isBefore(LocalTime.now().plusMinutes(time3))) {
            throw new OperationNotPermittedException("SLOT TIME SHOULD BE BEFORE MIDNIGHT and SHOULD BE OF 10 MINUTES or START TIME SHOULD BE 5 MINUTES AFTER CURRENT TIME");
        }

        List<Booking> bookings = bookingRepository.findAllBookingByCurrentDate(machineId,currentDate);

        int idx = 0;
        for (int i = 0; i < bookings.size(); i++) {
            Booking currentBooking = bookings.get(i);
            LocalTime currentEndTime = currentBooking.getEndTime();

            if(request.startTime().isBefore(currentEndTime)){
                break;
            }
            idx++;
        }
        if(bookings.isEmpty() || idx==bookings.size() || (bookings.size()==1 && request.endTime().isBefore(bookings.get(idx).getStartTime().minusMinutes(time3))) || (request.endTime().isBefore(bookings.get(idx).getStartTime().minusMinutes(time3)) && request.startTime().isAfter(bookings.get(idx-1).getEndTime().plusMinutes(time3)))){
            Booking booking = bookingMapper.toBooking(request);
            booking.setUser(user);
            var machine = machineRepository.findById(machineId);
            booking.setMachine(machine.get());

            System.out.println("making the thread when the booking ends update the amount of user");
            //now updating the users balance and the sotes revenue when the booking ends
            long minutesConsumed =  Duration.between(LocalTime.now(),booking.getEndTime()).plusMinutes(time4).toMinutes();
            if(minutesConsumed<=0)
                minutesConsumed = 3;
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(()->updateUserBalanceAndStoreRevenue(booking.getId(),machine.get().getStore().getId(),user.getId()),minutesConsumed, TimeUnit.MINUTES);

            bookingRepository.save(booking);
            return booking.getId();
        }
        else{
            throw new OperationNotPermittedException("Sorry can't book a machine due to wrong time slot");
        }
    }

    public PageResponse<BookingResponse> findAllBookingsOfMachine(Integer machineId, int page, int size) {
        Machine machine = machineRepository.findById(machineId).orElseThrow(()->new EntityNotFoundException("Machine not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Booking> bookings = bookingRepository.findAllByMachineId(pageable,machineId);
        List<BookingResponse> bookingResponses = bookings.stream().map(bookingMapper::toBookingResponse).toList();
        return new PageResponse<>(
                bookingResponses,
                bookings.getNumber(),
                bookings.getSize(),
                bookings.getTotalElements(),
                bookings.getTotalPages(),
                bookings.isFirst(),
                bookings.isLast()
        );
    }
}
