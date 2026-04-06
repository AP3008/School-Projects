#include <stdio.h>
#include <stdlib.h>
#include "snackaroo_dish_driver.h"
#include "snackaroo_dish.h"
#include "snackaroo_driver.h"
#include "snackaroo.h"

/*
Function: find_dish_by_code
Description: Given the head of the dish linked list and a dish code
searches through the linked list to find a dish structure with the matching code
*/

struct DishNode* find_dish_by_code(struct DishNode* head, int dishCode){
    struct DishNode* curr = head; 
    while(curr != NULL){
        if(curr->dish.dish_code == dishCode){
            return curr; 
        }
        curr = curr->next; 
    }
    return NULL; 
}

/*
Function: find_driver_by_code
Description: Given the head of the driver linked list and a driver code
searches through the linked list to find a driver structure with a matching code
*/

struct DriverNode* find_driver_by_code(struct DriverNode* head, int driverCode){
    struct DriverNode* curr = head; 
    while(curr != NULL){
        if(curr->driver.driver_code == driverCode){
            return curr; 
        } 
        curr = curr->next; 
    }
    return NULL; 
}

/*
Function: relationship_exists
Description: Given the head of the DishDriverCode linked list, a dish code, and a driver code
searches through the linked list to see if a relationship exists between these two codes
*/

int relationship_exists(struct DishDriverCode* rel_head, int dishCode, int driverCode){
    struct DishDriverCode* curr = rel_head; 
    while(curr != NULL){  // FIXED: was checking rel_head instead of curr
        if(curr->dish_code == dishCode && curr->driver_code == driverCode){
            return 1;
        } 
        curr = curr->next; 
    }
    return 0; 
}

/*
Function: inserts_relationship
Description: Inserts a new relationship in the DishDriverCode linked list when given a 
dish code and driver code. 
*/

int insert_relationship(struct DishDriverCode** rel_head, struct DishNode* dish_head, struct DriverNode* driver_head){
    int dishCode, driverCode, input_status;

    //Getting Dish code and chekcing if it exists 

    printf("\tEnter Dish Code: ");
    input_status = scanf("%d", &dishCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return 0;
    }

    if(!dish_code_exists(dishCode, dish_head)){
        printf("\tDish code doesn't exist.\n");
        return 0; 
    }

    //Getting driver code and checking if it exists 

    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return 0;
    }

    if(!driver_code_exists(driverCode, driver_head)){
        printf("\tDriver code doesn't exist.\n");
        return 0; 
    }

    //Making sure that this relationship doesn't already exist 

    if(relationship_exists(*rel_head, dishCode, driverCode)){
        printf("\tThis dish/driver relationship already exists.\n");
        return 0; 
    }

    //Adding the new relationship to the linked list 

    struct DishDriverCode* new_rel = (struct DishDriverCode*) malloc(sizeof(struct DishDriverCode)); 
    new_rel->dish_code = dishCode; 
    new_rel->driver_code = driverCode; 
    new_rel->next = NULL; 

    //If the linked list is empty we make the new relationship the head. 

    if(*rel_head == NULL){
        *rel_head = new_rel; 
    }
    else{

        //Otherwise we add the relationship at the end of the list. 
        struct DishDriverCode* curr = *rel_head; 
        while(curr->next != NULL){  // FIXED: was checking curr != NULL
            curr = curr->next; 
        }
        curr->next = new_rel; 
    }
    printf("\tRelationship added successfully\n"); 
    return 1; 
}

/*
Function: search_relationship
Description: Finds relationship based of dish code and driver code, and prints it out. 
*/

void search_relationship(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head){
    int dishCode, driverCode, input_status; 

    printf("\tEnter Dish Code: ");
    input_status = scanf("%d", &dishCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return;
    }
    
    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return;
    }

    if(!relationship_exists(rel_head, dishCode, driverCode)){
        printf("\tRelationship does not exist.\n");
        return; 
    } 

    struct DishNode* dish = find_dish_by_code(dish_head, dishCode); 
    struct DriverNode* driver = find_driver_by_code(driver_head, driverCode);  // FIXED: was passing driver_head twice
    
    printf("\n=== Dish/Driver Relationship ===\n");    
    printf("\nDish Details:\n");
    printf("Dish Code: %d\n", dishCode);
    printf("  Name: %s\n", dish->dish.dish_name);
    printf("  Restaurant: %s\n", dish->dish.restaurant_name);
    printf("  Rating: %.1f\n", dish->dish.rating);        
    printf("  Price: $%.2f\n", dish->dish.price);
    printf("\nDriver Details:\n");
    printf("Driver Code: %d\n", driverCode); 
    printf("  Name: %s\n", driver->driver.driver_name);
    printf("  Vehicle: %s\n", get_colour_name(driver->driver.vehicle_colour));
    printf("  Plate: %s\n", driver->driver.licence_plate);
}

/*
Function: print_dishes_for_driver
Description: Prints all dishes associated with one driver code 
*/
void print_dishes_for_driver(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head){
    int driverCode, input_status;
    
    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return;
    }
    
    struct DishDriverCode *curr = rel_head; 
    struct DishNode *target_dish; 
    int found = 0;
    
    printf("\n=== Dishes for Driver %d ===\n", driverCode);   
    
    while(curr != NULL){
        if(curr->driver_code == driverCode){
            target_dish = find_dish_by_code(dish_head, curr->dish_code); 
            if(target_dish != NULL){
                printf("\nDish Details:\n");
                printf("Dish Code: %d\n", curr->dish_code);
                printf("  Name: %s\n", target_dish->dish.dish_name);
                printf("  Restaurant: %s\n", target_dish->dish.restaurant_name);
                printf("  Rating: %.1f\n", target_dish->dish.rating);        
                printf("  Price: $%.2f\n\n", target_dish->dish.price);
                found = 1;
            }
        }
        curr = curr->next; 
    }
    
    if(!found){
        printf("\tNo dishes found for this driver.\n");
    }
}

/*
function: print_drivers_for_dish
description: prints drivers associated with one dish code. 
*/

void print_drivers_for_dish(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head){
    int dishCode, input_status;
    
    printf("\tEnter Dish Code: ");
    input_status = scanf("%d", &dishCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return;
    }
    
    struct DishDriverCode *curr = rel_head; 
    struct DriverNode *target_driver; 
    int found = 0;
    
    printf("\n=== Drivers for Dish %d ===\n", dishCode);   
    
    while(curr != NULL){
        if(curr->dish_code == dishCode){
            target_driver = find_driver_by_code(driver_head, curr->driver_code);
            if(target_driver != NULL){
                printf("\nDriver Details:\n");
                printf("Driver Code: %d\n", curr->driver_code); 
                printf("  Name: %s\n", target_driver->driver.driver_name);
                printf("  Vehicle: %s\n", get_colour_name(target_driver->driver.vehicle_colour));
                printf("  Plate: %s\n\n", target_driver->driver.licence_plate);
                found = 1;
            }
        }
        curr = curr->next;
    }
    
    if(!found){
        printf("\tNo drivers found for this dish.\n");
    }
}

/*
function: print_all
description: Prints all dish driver relationships in the DishDriverCode linked list
*/

void print_all(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head){
    if(rel_head == NULL){
        printf("\tNo relationships in database\n");
        return;
    }
    
    struct DishDriverCode *curr = rel_head; 
    struct DishNode *target_dish; 
    struct DriverNode *target_driver;
    
    printf("\n=== All Dish/Driver Relationships ===\n");   
    
    while(curr != NULL){
        target_dish = find_dish_by_code(dish_head, curr->dish_code);
        target_driver = find_driver_by_code(driver_head, curr->driver_code); 
        
        if(target_dish != NULL && target_driver != NULL){
            printf("\n--- Relationship ---\n");
            printf("Dish Code: %d\n", curr->dish_code);
            printf("  Name: %s\n", target_dish->dish.dish_name);
            printf("  Restaurant: %s\n", target_dish->dish.restaurant_name);
            printf("  Rating: %.1f\n", target_dish->dish.rating);        
            printf("  Price: $%.2f\n", target_dish->dish.price);
            printf("\nDriver Code: %d\n", curr->driver_code); 
            printf("  Name: %s\n", target_driver->driver.driver_name);
            printf("  Vehicle: %s\n", get_colour_name(target_driver->driver.vehicle_colour));
            printf("  Plate: %s\n", target_driver->driver.licence_plate);
        }
        curr = curr->next;
    }
}

/*
function: erase_relationship
description: Erases dish driver relationship if it exists 
(this doesn't affect the dishes itself just the relationship)
(allows dishes and drivers to be deleted)
*/

int erase_relationship(struct DishDriverCode **rel_head){
    int dishCode, driverCode, input_status;

    //Grabbing dish & driver code from user
    
    printf("\tEnter Dish Code: ");
    input_status = scanf("%d", &dishCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return 0;
    }
    
    printf("\tEnter Driver Code: ");
    input_status = scanf("%d", &driverCode);
    clearInputBuffer();
    
    if (input_status != 1) {
        printf("\tInvalid input. Please try again\n");
        return 0;
    }

    //If the linked list is empty we can just exit.

    if(*rel_head == NULL){
        printf("\tNo relationships in database\n");
        return 0;
    }

    //If the only relationship in the linked list has this code (i.e deleting the head)

    if ((*rel_head)->dish_code == dishCode && (*rel_head)->driver_code == driverCode) {
        struct DishDriverCode *temp = *rel_head;
        *rel_head = (*rel_head)->next;
        free(temp);
        printf("\tRelationship deleted successfully!\n");
        return 1;
    }

    //Searching for the relationship and using free for memory. 

    struct DishDriverCode *curr = *rel_head;
    struct DishDriverCode *prev = NULL;
    
    while (curr != NULL) {
        if (curr->dish_code == dishCode && curr->driver_code == driverCode) {
            prev->next = curr->next;
            free(curr);
            printf("\tRelationship deleted successfully!\n");
            return 1;
        }
        prev = curr;
        curr = curr->next;
    }
    
    printf("\tRelationship not found\n");
    return 0;
}
