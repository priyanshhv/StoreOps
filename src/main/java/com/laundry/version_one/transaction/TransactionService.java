package com.laundry.version_one.transaction;

import com.laundry.version_one.booking.Booking;
import com.laundry.version_one.booking.BookingRepository;
import com.laundry.version_one.exception.OperationNotPermittedException;
import com.laundry.version_one.machine.MachineRepository;
import com.laundry.version_one.store.StoreService;
import com.laundry.version_one.user.User;
import com.laundry.version_one.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BookingRepository bookingRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final StoreService storeService;

    public void stopTransaction(Integer transactionId, Integer bookingId){
        // Your logic to stop the transaction goes here
        // For example, update the transaction end time or perform any other necessary actions
        // ...
        System.out.println("Stopping transaction with ID: " + transactionId);
        //not authenticating as everything is authenticated
        //checking if the transaction is already closed or not
        Optional<Transaction> transaction = Optional.ofNullable(transactionRepository.findByBookingId(bookingId,LocalDate.now()));
        if(transaction.isEmpty())
        {
            throw new EntityNotFoundException("Transaction not found");
        }

        //checking if transaction is closed
        if(transaction.get().getEndTime()!=null){
            throw new OperationNotPermittedException("Transaction is already closed");
        }

        //else set the transaction closed time also set the machine off and the amount calculations

        transaction.get().setEndTime(LocalTime.now());

        Booking booking = transaction.get().getBooking();
        //off the machine
        //check if machine exists
        var machine = machineRepository.findById(booking.getMachine().getId()).orElseThrow(()->new EntityNotFoundException("Machine not found"));

        machine.setIsOn(Boolean.FALSE);
        machineRepository.save(machine);

        //now add the amount to the booking
        Double avgCostPerHour = machine.getAverageCostPerHour();

        Double hourConsumed = (double) Duration.between(transaction.get().getStartTime(),transaction.get().getEndTime()).toMinutes();
        hourConsumed /= 60.0;
        transaction.get().setAmount(hourConsumed*avgCostPerHour);


        Double bokingAmount = 0.0;
        if(booking.getTotalAmount()!=null)
            bokingAmount += (Double) booking.getTotalAmount();
        booking.setTotalAmount(bokingAmount+avgCostPerHour*hourConsumed);

        bookingRepository.save(booking);
        transactionRepository.save(transaction.get());

        System.out.println("\n\n Transaction is stoped now \n\n");
        return;
    }

    @Transactional
    public Integer save(TransactionRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        boolean hasOwnerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("OWNER"));
        if(hasOwnerRole)
        {
            throw new OperationNotPermittedException("Owners can't start/create a machine/transaction");
        }

        LocalTime currentTime = LocalTime.now();

        if(bookingRepository.findById(request.bookingId()).isEmpty()){
            throw new EntityNotFoundException("Booking id not found");
        }

        //check if the booking belongs to that user or not
        if(!(bookingRepository.findById(request.bookingId()).get().getUser().getId().equals(user.getId()))){
            throw new OperationNotPermittedException("Booking slot doesn't belongs to you");
        }

        if(!(bookingRepository.findById(request.bookingId()).get().getCreatedDate().equals(LocalDate.now()))){
            throw new OperationNotPermittedException("Booking is expired can't create a Transaction Creation Date doesn't match");
        }

        Booking booking = bookingRepository.findById(request.bookingId()).get();
        if(currentTime.isAfter(booking.getEndTime()) || currentTime.isBefore(booking.getStartTime())){
            throw new OperationNotPermittedException("Booking is either expired or not started can't create a Transaction");
        }

        //check if previous transaction is already created by the user or not which is still running
        //ensure that user can open only one transaction at a time for a given booking id
        Optional<List<Transaction>> transactions =Optional.ofNullable(transactionRepository.findAllByBookingId(booking.getId()));
        if(transactions.isPresent()&& !transactions.get().isEmpty()){
            Transaction transaction = transactions.get().get(transactions.get().size() - 1);
            if(transaction.getEndTime()==null)
                throw new OperationNotPermittedException("Previous Transaction haivng id as "+transaction.getId()+" is already open");
        }

        Transaction transaction = transactionMapper.toTransaction(request);
        transaction.setBooking(booking);

        //on the switch of the machine
        Integer machineId = booking.getMachine().getId();

        //check if machine exists
        var machine = machineRepository.findById(machineId).orElseThrow(()->new EntityNotFoundException("Machine not found"));
        transactionRepository.save(transaction);

        //on the machine
        machine.setIsOn(Boolean.TRUE);
        machineRepository.save(machine);

        //schedule the transaction to stop if not stopped

        long minutesConsumed =  Duration.between(currentTime,booking.getEndTime()).toMinutes();
        if(minutesConsumed==0)
            minutesConsumed = 1;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(()->stopTransaction(transaction.getId(), booking.getId()),minutesConsumed, TimeUnit.MINUTES);

        System.out.println("\n\n Minutes Consumed = "+minutesConsumed+" minutes");
        return transaction.getId();
    }

    public Integer close(TransactionRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        boolean hasOwnerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("OWNER"));
        if(hasOwnerRole)
        {
            throw new OperationNotPermittedException("Owners can't start/create a machine/transaction");
        }

        LocalTime currentTime = LocalTime.now();

        //check if the booking id is not found
        if(bookingRepository.findById(request.bookingId()).isEmpty()){
            throw new EntityNotFoundException("Booking id not found");
        }

        //check if the booking belongs to that user or not
        if(!(bookingRepository.findById(request.bookingId()).get().getUser().getId().equals(user.getId()))){
            throw new OperationNotPermittedException("Booking slot doesn't belongs to you");
        }

        //if booking is expired then assuming transaction would be closed automatically
        if(!(bookingRepository.findById(request.bookingId()).get().getCreatedDate().equals(LocalDate.now()))){
            throw new OperationNotPermittedException("Booking is expired can't create a Transaction Creation Date doesn't match");
        }

        Booking booking = bookingRepository.findById(request.bookingId()).get();
        if(currentTime.isAfter(booking.getEndTime()) || currentTime.isBefore(booking.getStartTime())){
            throw new OperationNotPermittedException("Booking is expired can't create a Transaction");
        }

        //checking if the transaction is already closed or not
        Optional<Transaction> transaction = Optional.ofNullable(transactionRepository.findByBookingId(booking.getId(),LocalDate.now()));
        if(transaction.isEmpty())
        {
            throw new EntityNotFoundException("Transaction not found");
        }

        //cheching if transaction is closed
        if(transaction.get().getEndTime()!=null){
            throw new OperationNotPermittedException("Transaction is already closed");
        }

        //else set the transaction closed time also set the machine off and the amount calculations

        transaction.get().setEndTime(LocalTime.now());

        //off the machine
        //check if machine exists
        var machine = machineRepository.findById(booking.getMachine().getId()).orElseThrow(()->new EntityNotFoundException("Machine not found"));

        machine.setIsOn(Boolean.FALSE);
        machineRepository.save(machine);

        //now add the amount to the booking
        Double avgCostPerHour = machine.getAverageCostPerHour();

        Double hourConsumed = (double) Duration.between(transaction.get().getStartTime(),transaction.get().getEndTime()).toMinutes();
        hourConsumed /= 60.0;
        transaction.get().setAmount(hourConsumed*avgCostPerHour);


        Double bokingAmount = 0.0;
        if(booking.getTotalAmount()!=null)
            bokingAmount += (Double) booking.getTotalAmount();
        booking.setTotalAmount(bokingAmount+avgCostPerHour*hourConsumed);

        bookingRepository.save(booking);
        transactionRepository.save(transaction.get());
        return transaction.get().getId();
    }


}
