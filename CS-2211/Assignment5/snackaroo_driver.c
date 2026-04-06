
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "snackaroo.h"
#include "snackaroo_dish.h"
#include "snackaroo_driver.h"
#include "snackaroo_dish_driver.h"
#include "snackaroo_file_io.h"

/*
Function: is_valid_licence_plate
Description: Validates that a licence plate is between 2-8 characters
and contains only alphanumeric characters or spaces
*/

int is_valid_licence_plate(const char* plate){
    int len = strlen(plate); 
    if(len < MIN_LICENCE_LENGTH || len > MAX_LICENCE_LENGTH){
        return 0; 
    }
    for(int i = 0; i < len; i++){
        if(!isalnum(plate[i]) && plate[i] != ' '){
            return 0; 
        }
    }
    return 1; 
}

/*
Function: driver_code_exists
Description: Checks if a driver code already exists in the driver linked list
*/

int driver_code_exists(int code, struct DriverNode* head){
    struct DriverNode* curr = head; 
    while(curr != NULL){
        if(curr->driver.driver_code == code){
            return 1; 
        }
        curr = curr->next; 
    }
    return 0; 
}

/*
Function: get_colour_name
Description: Converts a vehicle colour integer code to its string representation
*/


const char* get_colour_name(int colour){
    switch(colour){
        case COLOUR_RED:   
            return "red";
        case COLOUR_GREEN: 
            return "green";
        case COLOUR_BLUE:  
            return "blue";
        case COLOUR_GREY:  
            return "grey";
        case COLOUR_WHITE: 
            return "white";
        case COLOUR_BLACK: 
            return "black";
        case COLOUR_OTHER: 
            return "other";
        default:           
            return "invalid";
    }
}

/*
Function: insert_driver
Description: Inserts a new driver into the driver linked list.
Prompts user for driver code, name, vehicle colour, and licence plate.
Validates all inputs before insertion.
*/

int insert_driver(struct DriverNode** head){
    int driverCode; 
    int input_status;

    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    if(input_status != 1){
        printf("\tInvalid input. Please try again\n");
        return 0; 
    }
    if(driverCode <= 0){
        printf("\tDriver code must be positive\n");
        return 0; 
    }
    if(driver_code_exists(driverCode, *head)){
        printf("\tYou entered a duplicate ID\n");
        return 0; 
    }

    char driverName[MAX_DRIVER_NAME_LENGTH];
    char licencePlate[MAX_LICENCE_LENGTH];
    int vehicleColour; 
    clearInputBuffer(); 
    printf("\tEnter driver name: ");
    fgets(driverName, sizeof(driverName), stdin);
    driverName[strcspn(driverName, "\n")] = 0;  // Remove newline
    
    if (strlen(driverName) >= MAX_DRIVER_NAME_LENGTH - 1) {
        printf("\tDriver name too long\n");
        return 0;
    }

    while (1) {
        printf("\tEnter vehicle colour (0=red, 1=green, 2=blue, 3=grey, 4=white, 5=black, 6=other): ");
        input_status = scanf("%d", &vehicleColour);
        clearInputBuffer();  
        
        if (input_status != 1) {
            printf("\tInvalid input. Please try again\n"); 
        }
        else if (vehicleColour >= MIN_COLOUR && vehicleColour <= MAX_COLOUR) {
            break; 
        }
        else {
            printf("\tEnter a number between 0 and 6\n");
        }
    }

    while (1) {
        printf("\tEnter licence plate (2-8 alphanumeric characters or spaces): ");
        fgets(licencePlate, sizeof(licencePlate), stdin);
        licencePlate[strcspn(licencePlate, "\n")] = 0;
        
        if (is_valid_licence_plate(licencePlate)) {
            break;
        }
        else {
            printf("\tInvalid licence plate. Must be 2-8 characters (alphanumeric and spaces only)\n");
        }
    }

    struct DriverNode* new_node = (struct DriverNode*)malloc(sizeof(struct DriverNode));
    
    if (new_node == NULL) {
        printf("\tMemory allocation failed\n");
        return 0;
    }

    new_node->driver.driver_code = driverCode;
    new_node->driver.vehicle_colour = vehicleColour;
    strcpy(new_node->driver.driver_name, driverName);
    strcpy(new_node->driver.licence_plate, licencePlate);
    new_node->next = NULL;

    // Insert at the end of the list
    if (*head == NULL) {
        // List is empty, new node becomes head
        *head = new_node;
    } 
    else {
        // Traverse to the end
        struct DriverNode *curr = *head; 
        while (curr->next != NULL) {
            curr = curr->next; 
        }
        // Add new node at the end
        curr->next = new_node; 
    }

    printf("\tDriver added successfully!\n");
    clearInputBuffer(); 
    return 1;
}

/*
Function: search_driver
Description: Searches for a driver by driver code and prints their details
*/

void search_driver(struct DriverNode* head){
    int driverCode; 
    int input_status; 

    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    clearInputBuffer();

    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return; 
    }

    if(!driver_code_exists(driverCode, head)){
        printf("\tThe code does not exist.\n");
        return; 
    }

    struct DriverNode* curr = head; 
    
    while (curr != NULL) {
        if (curr->driver.driver_code == driverCode) {
            // Found the driver - print it
            printf("\n%-12s %-25s %-15s %-15s\n", 
                   "Driver Code", "Driver Name", "Vehicle Colour", "Licence Plate");
            printf("================================================================================\n");
            printf("%-12d %-25s %-15s %-15s\n", 
                   curr->driver.driver_code, 
                   curr->driver.driver_name, 
                   get_colour_name(curr->driver.vehicle_colour),
                   curr->driver.licence_plate);
            return;
        }  
        curr = curr->next; 
    }
    
    printf("\tDriver code %d not found\n", driverCode);
}

/*
Function: update_driver
Description: Updates an existing driver's information based on driver code
*/

int update_driver(struct DriverNode* head){
    int driverCode; 
    int input_status; 

    printf("\tEnter Driver Code: ");
    input_status = scanf(" %d", &driverCode);
    clearInputBuffer();

    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return 0; 
    }

    // Find the driver
    struct DriverNode *curr = head;
    struct DriverNode *target_node = NULL;  
    
    while (curr != NULL) {
        if (curr->driver.driver_code == driverCode) {
            target_node = curr;
            break;  // Found it, stop searching
        }
        curr = curr->next;
    }

    if (target_node == NULL) {
        printf("\tThe driver code was not found, please try again.\n");
        return 0; 
    }

    // Get new driver information
    char driverName[MAX_DRIVER_NAME_LENGTH]; 
    char licencePlate[MAX_LICENCE_LENGTH];
    int vehicleColour;

    printf("\tEnter new driver name: ");
    fgets(driverName, sizeof(driverName), stdin);
    driverName[strcspn(driverName, "\n")] = 0;
    
    if (strlen(driverName) >= MAX_DRIVER_NAME_LENGTH - 1) {
        printf("\tDriver name too long\n");
        return 0;
    }

    // Get vehicle colour with validation
    while (1) {
        printf("\tEnter new vehicle colour (0=red, 1=green, 2=blue, 3=grey, 4=white, 5=black, 6=other): ");
        input_status = scanf("%d", &vehicleColour);
        clearInputBuffer();  
        
        if (input_status != 1) {
            printf("\tInvalid input. Please try again\n"); 
        }
        else if (vehicleColour >= 0 && vehicleColour <= 6) {
            break; 
        }
        else {
            printf("\tEnter a number between 0 and 6\n");
        }
    }

    // Get licence plate with validation
    while (1) {
        printf("\tEnter new licence plate (2-8 alphanumeric characters or spaces): ");
        fgets(licencePlate, sizeof(licencePlate), stdin);
        licencePlate[strcspn(licencePlate, "\n")] = 0;
        
        if (is_valid_licence_plate(licencePlate)) {
            break;
        }
        else {
            printf("\tInvalid licence plate. Must be 2-8 characters (alphanumeric and spaces only)\n");
        }
    }

    // Update the driver in place
    target_node->driver.vehicle_colour = vehicleColour;
    strcpy(target_node->driver.driver_name, driverName);
    strcpy(target_node->driver.licence_plate, licencePlate);

    printf("\tDriver updated successfully!\n");
    clearInputBuffer();
    return 1;
}

/*
Function: print_all_drivers
Description: Prints all drivers in the driver linked list
*/

void print_all_drivers(struct DriverNode* head){
    if (head == NULL) {
        printf("\tNo drivers in database\n");
        return;
    }

    printf("\n%-12s %-25s %-15s %-15s\n", 
           "Driver Code", "Driver Name", "Vehicle Colour", "Licence Plate");
    printf("====================================================================\n");
    
    struct DriverNode *curr = head; 
    
    while (curr != NULL) {
        printf("%-12d %-25s %-15s %-15s\n",   
               curr->driver.driver_code, 
               curr->driver.driver_name, 
               get_colour_name(curr->driver.vehicle_colour),
               curr->driver.licence_plate);
        curr = curr->next; 
    }
    printf("\n");
}

/*
Function: erase_driver
Description: Erases a driver from the linked list based on driver code.
Cannot erase a driver that is in a relationship.
*/

int erase_driver(struct DriverNode** head, struct DishDriverCode* rel_head){
    int driverCode; 
    int input_status; 

    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    clearInputBuffer();

    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return 0; 
    }

    if (*head == NULL) {
        printf("\tNo drivers in database\n");
        return 0;
    }

    struct DishDriverCode *rel_finder = rel_head; 
    while(rel_finder != NULL){
        if(relationship_exists(rel_head, rel_finder->dish_code, driverCode)){
            printf("\tA relationship with this code already exists, it cannot be removed.\n");
            return 0; 
        }
        rel_finder = rel_finder->next; 
    }

    // deleting the first node (head)
    if ((*head)->driver.driver_code == driverCode) {
        struct DriverNode *temp = *head;   // Save pointer to node we're deleting
        *head = (*head)->next;              // Move head to next node
        free(temp);                         // Free the memory
        printf("\tDriver deleted successfully!\n");
        return 1; 
    }

    // deleting a node in the middle or end
    struct DriverNode *curr = *head;
    struct DriverNode *prev = NULL;
    
    while (curr != NULL) {
        if (curr->driver.driver_code == driverCode) {
            prev->next = curr->next;  
            free(curr);               
            printf("\tDriver deleted successfully\n");
            return 1;
        }
        prev = curr;
        curr = curr->next;
    }

    // If we get here, driver was not found
    printf("\tThe driver code was not found\n");
    return 0; 
}
