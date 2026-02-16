package be.loisirs.tfe2025.plateforme_loisirs.api.controller;

import be.loisirs.tfe2025.plateforme_loisirs.model.*;
import be.loisirs.tfe2025.plateforme_loisirs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

//    @Autowired
//    private PartnerRepository partnerRepository;
//
//    @Autowired
//    private AddressRepository addressRepository;
//
//    @Autowired
//    private ActivityRepository activityRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private ActivityCategoryRepository activityCategoryRepository;
//
//    @Autowired
//    private ImageRepository imageRepository;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//
//    @Autowired
//    private UserSubscriptionRepository userSubscriptionRepository;
//
//    @Autowired
//    private OrdersRepository ordersRepository;
//
//    @Autowired
//    private PaymentRepository paymentRepository;

    // ===============================
    // USERS
    // ===============================
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // ===============================
    // ROLES
    // ===============================
    @GetMapping("/roles")
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @GetMapping("/user_roles")
    public List<UserRole> getUserRoles() {
        return userRoleRepository.findAll();
    }

    // ===============================
    // PARTNERS & ADDRESS
    // ===============================
//    @GetMapping("/partners")
//    public List<Partner> getPartners() {
//        return partnerRepository.findAll();
//    }
//
//    @GetMapping("/addresses")
//    public List<Address> getAddresses() {
//        return addressRepository.findAll();
//    }
//
//    // ===============================
//    // ACTIVITIES / CATEGORY / IMAGE
//    // ===============================
//    @GetMapping("/activities")
//    public List<Activity> getActivities() {
//        return activityRepository.findAll();
//    }
//
//    @GetMapping("/categories")
//    public List<Category> getCategories() {
//        return categoryRepository.findAll();
//    }
//
//    @GetMapping("/activity_categories")
//    public List<ActivityCategory> getActivityCategories() {
//        return activityCategoryRepository.findAll();
//    }
//
//    @GetMapping("/images")
//    public List<Image> getImages() {
//        return imageRepository.findAll();
//    }
//
//    // ===============================
//    // RESERVATIONS
//    // ===============================
//    @GetMapping("/reservations")
//    public List<Reservation> getReservations() {
//        return reservationRepository.findAll();
//    }
//
//    // ===============================
//    // SUBSCRIPTIONS / USER_SUBSCRIPTIONS
//    // ===============================
//    @GetMapping("/subscriptions")
//    public List<Subscription> getSubscriptions() {
//        return subscriptionRepository.findAll();
//    }
//
//    @GetMapping("/user_subscriptions")
//    public List<UserSubscription> getUserSubscriptions() {
//        return userSubscriptionRepository.findAll();
//    }
//
//    // ===============================
//    // ORDERS / PAYMENTS
//    // ===============================
//    @GetMapping("/orders")
//    public List<Orders> getOrders() {
//        return ordersRepository.findAll();
//    }
//
//    @GetMapping("/payments")
//    public List<Payment> getPayments() {
//        return paymentRepository.findAll();
//    }
}
