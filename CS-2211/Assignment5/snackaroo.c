
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
Function: help
Description: Prints the main help menu showing all available commands
*/

void help(){
    printf("\n========================================\n");
    printf("\tSNACKAROO HELP MENU\n");
    printf("========================================\n");
    printf("Available Commands:\n");
    printf("  h - Print this help menu\n");
    printf("  q - Quit the program (all data will be lost)\n");
    printf("  m - Control dishes (see dish menu)\n");
    printf("  a - Control drivers (see driver menu)\n");
    printf("  r - Control relationships (see relationship menu)\n");
    printf("  d - Dump database\n");
    printf("  f - Restore database\n");
    printf("========================================\n\n");
}

/*
Function: print_dish_menu
Description: Prints the dish management submenu options
*/

void print_dish_menu() {
    printf("\n--- Dish Management Menu ---\n");
    printf("  i - Insert a new dish\n");
    printf("  s - Search for a dish\n");
    printf("  u - Update a dish\n");
    printf("  p - Print all dishes\n");
    printf("  e - Erase a dish\n");
    printf("  b - Back to main menu\n");
}

/*
Function: print_driver_menu
Description: Prints the driver management submenu options
*/

void print_driver_menu() {
    printf("\n--- Driver Management Menu ---\n");
    printf("  i - Insert a new driver\n");
    printf("  s - Search for a driver\n");
    printf("  u - Update a driver\n");
    printf("  p - Print all drivers\n");
    printf("  e - Erase a driver\n");
    printf("  b - Back to main menu\n");
}

/*
Function: print_relationship_menu
Description: Prints the relationship management submenu options
*/

void print_relationship_menu() {
    printf("\n--- Relationship Management Menu ---\n");
    printf("  i - Insert a new relationship\n");
    printf("  s - Search for a relationship\n");
    printf("  p - Print all relationships\n");
    printf("  d - Print all dishes for a driver\n");
    printf("  r - Print all drivers for a dish\n");
    printf("  e - Erase a relationship\n");
    printf("  b - Back to main menu\n");
}

/*
Function: handle_dish_menu
Description: Handles user interaction within the dish management menu.
Continuously prompts for commands until user returns to main menu.
*/

void handle_dish_menu(struct DishNode** dish_head, struct DriverNode* driver_head, struct DishDriverCode* rel_head){
    char command;
    int isRunning = 1;
    print_dish_menu();

    while (isRunning) {
        printf("\nEnter command: ");
        scanf(" %c", &command);
        clearInputBuffer();
        
        switch(command) {
            case 'i':
                insert_dish(dish_head);
                break;
            case 's':
                search_dish(*dish_head);
                break;
            case 'u':
                update_dish(*dish_head);
                break;
            case 'p':
                print_all_dishes(*dish_head);
                break;
            case 'e':
                erase_dish(dish_head, rel_head);
                break;
            case 'b':
                printf("Returning to main menu...\n");
                isRunning = 0;
                break;
            default:
                printf("Invalid command. Please try again.\n");
        }
    }
}

/*
Function: handle_driver_menu
Description: Handles user interaction within the driver management menu.
Continuously prompts for commands until user returns to main menu.
*/

void handle_driver_menu(struct DriverNode** driver_head, struct DishNode* dish_head, struct DishDriverCode* rel_head){
    char command; 
    int isRunning = 1; 
    print_driver_menu();

    while(isRunning){
        printf("\nEnter a command: ");
        scanf(" %c", &command);
        clearInputBuffer(); 

        switch(command){
            case 'i':
                insert_driver(driver_head);
                break; 
            case 's': 
                search_driver(*driver_head);
                break;
            case 'u':
                update_driver(*driver_head);
                break; 
            case 'p':
                print_all_drivers(*driver_head); 
                break; 
            case 'e':
                erase_driver(driver_head, rel_head);
                break; 
            case 'b':
                printf("Returning to main menu...\n");
                isRunning = 0; 
                break;
            default:
                printf("Invalid command. Please try again.\n");
        }
    }
}

/*
Function: handle_relationship_menu
Description: Handles user interaction within the relationship management menu.
Continuously prompts for commands until user returns to main menu.
*/

void handle_relationship_menu(struct DishDriverCode** rel_head, struct DishNode* dish_head, struct DriverNode* driver_head){
    char command; 
    int isRunning = 1; 
    print_relationship_menu();

    while(isRunning){
        printf("\nEnter a command: ");
        scanf(" %c", &command);
        clearInputBuffer(); 

        switch(command){
            case 'i':
                insert_relationship(rel_head, dish_head, driver_head);
                break; 
            case 's': 
                search_relationship(*rel_head, dish_head, driver_head);
                break;
            case 'p':
                print_all(*rel_head, dish_head, driver_head); 
                break; 
            case 'd':
                print_dishes_for_driver(*rel_head, dish_head, driver_head);
                break;
            case 'r':
                print_drivers_for_dish(*rel_head, dish_head, driver_head);
                break;
            case 'e':
                erase_relationship(rel_head);
                break; 
            case 'b':
                printf("Returning to main menu...\n");
                isRunning = 0; 
                break;
            default:
                printf("Invalid command. Please try again.\n");
        }
    }
}

/*
Function: free_dish_list
Description: Frees all memory allocated for the dish linked list
*/

void free_dish_list(struct DishNode* head){
    struct DishNode* curr = head; 
    struct DishNode* tmp; 
    while(curr != NULL){
        tmp = curr->next; 
        free(curr);
        curr = tmp; 
    }
}

/*
Function: free_driver_list
Description: Frees all memory allocated for the driver linked list
*/

void free_driver_list(struct DriverNode* head){
    struct DriverNode* curr = head; 
    struct DriverNode* tmp; 
    while(curr != NULL){
        tmp = curr->next;
        free(curr);
        curr = tmp; 
    }
}

/*
Function: free_relationship_list
Description: Frees all memory allocated for the relationship linked list
*/

void free_relationship_list(struct DishDriverCode* head){
    struct DishDriverCode* curr = head; 
    struct DishDriverCode* tmp; 
    while(curr != NULL){
        tmp = curr->next;
        free(curr);
        curr = tmp; 
    }
}

int main(){
    struct DishNode* dish_head = NULL;
    struct DriverNode* driver_head = NULL; 
    struct DishDriverCode* rel_head = NULL;  
    printf("**********************\n");
    printf("* 2211 Snackaro 2.0  *\n"); 
    printf("**********************\n");   

    char input; 
    while(1){
        printf("Enter operation code (h for help): ");
        scanf(" %c", &input);
        clearInputBuffer(); 
        if(input == 'h'){
            help(); 
        }
        else if (input == 'q'){
            printf("Exiting Program\n"); 
            break; 
        }
        else if (input == 'm'){
            handle_dish_menu(&dish_head, driver_head, rel_head); 
        }
        else if (input == 'a'){
            handle_driver_menu(&driver_head, dish_head, rel_head);
        }
        else if(input == 'r'){
            handle_relationship_menu(&rel_head, dish_head, driver_head);
        }
        else if(input == 'd'){
            // Dump database
            printf("\n--- Dump Menu ---\n");
            printf("  1 - Dump dishes and drivers\n");
            printf("  2 - Dump relationships\n");
            printf("  3 - Dump everything\n");
            printf("Enter choice: ");
            
            int choice;
            scanf("%d", &choice);
            clearInputBuffer();
            
            switch(choice){
                case 1:
                    dump_database(dish_head, driver_head);
                    break;
                case 2:
                    dump_relationships(rel_head);
                    break;
                case 3:
                    dump_database(dish_head, driver_head);
                    dump_relationships(rel_head);
                    break;
                default:
                    printf("Invalid choice\n");
            }
        }
        else if(input == 'f'){
            // Restore database
            printf("\n--- Restore Menu ---\n");
            printf("  1 - Restore dishes and drivers\n");
            printf("  2 - Restore relationships\n");
            printf("  3 - Restore everything\n");
            printf("Enter choice: ");
            
            int choice;
            scanf("%d", &choice);
            clearInputBuffer();
            
            switch(choice){
                case 1:
                    restore_database(&dish_head, &driver_head);
                    break;
                case 2:
                    restore_relationships(&rel_head, dish_head, driver_head);
                    break;
                case 3:
                    restore_database(&dish_head, &driver_head);
                    restore_relationships(&rel_head, dish_head, driver_head);
                    break;
                default:
                    printf("Invalid choice\n");
            }
        }
        else{
            printf("Invalid input, please try again.\n");
        }
    }
    free_dish_list(dish_head); 
    free_driver_list(driver_head); 
    free_relationship_list(rel_head); 
    return 0; 
}
