# Food Delivery Web Application

A full-stack Food Delivery System built using Spring Boot, MySQL, and Razorpay.  
The application allows users to browse food items, place orders, make secure payments, and track delivery status. It also includes an Admin Panel for managing menu items.

---

## Features

### User Features
- Browse dynamic food menu  
- Add items to cart with quantity  
- Make secure online payments using Razorpay  
- View order history  
- Track order status  

### Admin Features
- Add new menu items  
- Edit existing items  
- Delete menu items  
- Manage food catalog  

---

## Authentication and Security
- User Signup and Login  
- Password encryption using BCrypt  
- Role-based access control (USER / ADMIN)  
- Session-based authentication  

---

## Payment Integration
- Integrated Razorpay Payment Gateway (Test Mode)  
- Secure payment verification using:
  - razorpay_payment_id  
  - razorpay_order_id  
  - razorpay_signature  
- HMAC SHA256 signature validation  
- Payment details stored in database  

---

## Database Schema
- Users: Stores user details and roles  
- Menu: Food items  
- Orders: Order summary  
- OrderItems: Individual items in each order  

---

## Application Flow
1. User selects food items  
2. Adds items to cart  
3. Proceeds to payment  
4. Razorpay payment popup opens  
5. Payment is completed  
6. Backend verifies payment signature  
7. Order is stored in database  
8. User can track delivery status  

---

## Tech Stack
- Frontend: HTML, CSS, Bootstrap, Thymeleaf  
- Backend: Java, Spring Boot  
- Database: MySQL  
- Payment Gateway: Razorpay (Test Mode)  
- Architecture: MVC  

---

## Note
- Payments are tested using Razorpay Test Mode  
- UPI may require full account activation; hence card payments are used for testing  

---

## Outcome
This project demonstrates:
- Full-stack web development  
- Payment gateway integration  
- Secure backend logic  
- Database design and management

# Screenshots:

## Login page

<img width="648" height="525" alt="image" src="https://github.com/user-attachments/assets/445577fa-add5-4a58-9bb7-48d5b33404d9" />

## Admin page

<img width="1839" height="684" alt="image" src="https://github.com/user-attachments/assets/c942e649-6b8f-4f4d-aae4-f069d0e9e332" />

## Menu page

<img width="1834" height="700" alt="image" src="https://github.com/user-attachments/assets/6043833e-b0c9-4694-a8de-1f42500178f1" />
<img width="1848" height="687" alt="image" src="https://github.com/user-attachments/assets/22b58d64-73a1-42b6-bc26-5cbb27f28436" />
<img width="1817" height="884" alt="image" src="https://github.com/user-attachments/assets/4a12e043-92f3-4eb8-98ca-fcf4ca89c3a8" />

## Carts page

<img width="1860" height="650" alt="image" src="https://github.com/user-attachments/assets/fbd615c3-9d4b-448f-b3f5-4a99106972a0" />

## Payment page 

<img width="966" height="482" alt="image" src="https://github.com/user-attachments/assets/fed9fbc7-66b9-460c-aed9-b21caeb3fc03" />
<img width="1485" height="674" alt="image" src="https://github.com/user-attachments/assets/00e285cf-b07d-467e-a155-e3e9ae3ad5c6" />
<img width="1347" height="759" alt="image" src="https://github.com/user-attachments/assets/f352b3d3-0ade-46fa-b431-a99725b8a3ff" />
<img width="1357" height="730" alt="image" src="https://github.com/user-attachments/assets/1ad73c0a-ea91-4e8d-86bc-5ab7f0cf980d" />
<img width="1885" height="697" alt="image" src="https://github.com/user-attachments/assets/9bbf9d7e-5acc-4bb1-9f1f-ec7c6d95f0dd" />

## Tracking page

<img width="1742" height="703" alt="image" src="https://github.com/user-attachments/assets/fcc060d8-3126-4897-98c6-5fbfb28ffb22" />

# Demo Video
[Click here to watch the video]-(https://github.com/user-attachments/assets/9c0a6bd0-4cbf-462d-b6f2-5391a479b37a)
