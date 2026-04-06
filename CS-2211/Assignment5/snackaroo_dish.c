
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
function clearInputBuffer (helper)
description: Clears input line until null terminator so we
don't use characters in following input which would result in 
unexpected output. 
*/

void clearInputBuffer() {
    int c;
    while ((c = getchar()) != '\n' && c != EOF);
}

/*
Function: dish_code_exists
Description: Finds out whether a dish exists based on 
dish code.
*/

int dish_code_exists(int code, struct DishNode* head){

    //Traversing linked list until we find the dish code. 

    struct DishNode *curr = head; 

    while(curr != NULL){
        if(curr->dish.dish_code == code){
            return 1; 
        }
        curr = curr->next; 
    }
   return 0;  
}

/*
function: insert_dish
description: inserts dish into dish linked list
*/

int insert_dish(struct DishNode** head){
    int dishCode; 
    int input_status; 

    printf("\tEnter Dish Code: ");
    input_status = scanf("%d", &dishCode);
    clearInputBuffer(); 

    if(input_status != 1){
        printf("\tInvalid input. Please try again\n");
        return 0; 
    }

    //Checking if code exists already 

    if(dish_code_exists(dishCode, *head)){
        printf("\tYou entered a duplicate ID\n"); 
        return 0; 
    }

    char dishName[100]; 
    char restaurantName[100];
    float avgRating;   
    float price; 

    printf("\tEnter a dish name: ");
    fgets(dishName, sizeof(dishName),stdin);
    dishName[strcspn(dishName,"\n")] = 0;
    if(strlen(dishName) >= 99){
        clearInputBuffer();
    }
    printf("\tEnter the name of the restaurant: ");
    fgets(restaurantName, sizeof(restaurantName), stdin);
    restaurantName[strcspn(restaurantName,"\n")] = 0; 
    if(strlen(restaurantName) >= 99){
        clearInputBuffer(); 
    }

    while(1){
        printf("\tEnter the average rating: ");
        input_status = scanf("%f", &avgRating);
        clearInputBuffer();  
        if(input_status != 1){
            printf("\tInvalid input. Please try again\n"); 
        }
        else if(avgRating >= 0.0 && avgRating <= 10.0){
            break; 
        }
        printf("\tEnter a number between 0.0 - 10.0\n");
    }
    while(1){
        printf("\tEnter the price: "); 
        input_status = scanf("%f", &price);
        clearInputBuffer(); 
        if(input_status != 1){
            printf("\tInvalid input. Please try again\n"); 
        }
        else if(price > 0.00){
            break; 
        }
        printf("\tEnter a price greater than 0.0\n");
    }

    struct DishNode* new_node = (struct DishNode*)malloc(sizeof(struct DishNode)); 
    new_node->dish.dish_code = dishCode;
    new_node->dish.rating = avgRating;
    new_node->dish.price = price;
    strcpy(new_node->dish.dish_name, dishName);
    strcpy(new_node->dish.restaurant_name, restaurantName);
    new_node->next = NULL;

    //If linked list it empty we make it the head

    if(*head == NULL){
        *head = new_node; 
        return 1; 
    }
    else{

        //Otherwise we put it at the end. 

        struct DishNode *curr = *head; 
        while(curr->next != NULL){
            curr = curr->next; 
        }
        curr->next = new_node;
        printf("\tDish added successfully\n");
        return 1;  
    }
}


/*
function: search_dish
description: Searches for dish and prints it out based on provided dish code. 
*/
void search_dish(struct DishNode* head){
    int dishCode; 
    int input_status; 

    printf("\tEnter Dish Code: ");
    input_status = scanf(" %d", &dishCode);
    clearInputBuffer();

    if(input_status != 1){
        printf("\tInvalid input. Please try again\n");
        return; 
    }

    //Making sure the dish exists 

    if(dish_code_exists(dishCode, head) == 0){
        printf("\tThat code does not exist.\n");
        return; 
    }

    struct DishNode* curr = head; 

    while(curr != NULL){
        if(curr->dish.dish_code == dishCode){
            printf("%-10s %-25s %-35s %-10s %-10s\n", "Dish Code", "Dish Name", "Restaurant Name", "Dish Rating", "Price"); 
            printf("================================================================================\n");
            printf("%-10d %-25s %-35s %-10.1f %-10.2f\n", curr->dish.dish_code, curr->dish.dish_name, curr->dish.restaurant_name, curr->dish.rating, curr->dish.price);
            return; 
        }  
        curr = curr->next; 
    }
}

/*
function: update_dish
description: Updates dish at dish code 
*/

int update_dish(struct DishNode* head){
    int dishCode, input_status; 

    printf("\tEnter Dish Code: ");
    input_status = scanf(" %d", &dishCode);
    clearInputBuffer(); 

    if(input_status != 1){
        printf("\tInvalid input. Please try again\n");
        return 0; 
    }

    //Making sure that the dish exists 

    if(dish_code_exists(dishCode, head) == 0){
        printf("\tThat code does not exist\n");
        return 0; 
    }
    struct DishNode *curr = head;
    struct DishNode *target_node = NULL;  
    while(curr != NULL){
        if(curr->dish.dish_code == dishCode){
            target_node = curr; 
        }
        curr = curr->next; 
    }

    if(target_node == NULL){
        printf("The dish code was not found, please try again.");
        return 0; 
    }

    char dishName[100]; 
    char restaurantName[100];
    float avgRating;   
    float price; 

    printf("\tEnter a dish name: ");
    fgets(dishName, sizeof(dishName),stdin);
    dishName[strcspn(dishName,"\n")] = 0;
    if(strlen(dishName) >= 99){
        clearInputBuffer();
    }
    printf("\tEnter the name of the restaurant: ");
    fgets(restaurantName, sizeof(restaurantName), stdin);
    restaurantName[strcspn(restaurantName,"\n")] = 0; 
    if(strlen(restaurantName) >= 99){
        clearInputBuffer(); 
    }

    while(1){
        printf("\tEnter the average rating: ");
        input_status = scanf("%f", &avgRating);
        clearInputBuffer();  
        if(input_status != 1){
            printf("\tInvalid input. Please try again\n"); 
        }
        else if(avgRating >= 0.0 && avgRating <= 10.0){
            break; 
        }
        printf("\tEnter a number between 0.0 - 10.0\n");
    }
    while(1){
        printf("\tEnter the price: "); 
        input_status = scanf("%f", &price);
        clearInputBuffer(); 
        if(input_status != 1){
            printf("\tInvalid input. Please try again\n"); 
        }
        else if(price > 0.00){
            break; 
        }
        printf("\tEnter a price greater than 0.0\n");
    }

    //Updates values at that linked list node 

    target_node->dish.rating = avgRating;
    target_node->dish.price = price;
    strcpy(target_node->dish.dish_name, dishName);
    strcpy(target_node->dish.restaurant_name, restaurantName);

    printf("\tDish updated successfully!\n");

    return 1;  

}

/*
function: print_all_dishes
description: prints all dishes in the dishes linked list
*/

void print_all_dishes(struct DishNode* head){
    if(head == NULL){
        printf("\tNo dishes in database\n"); 
        return; 
    }
    printf("%-10s %-25s %-35s %-10s %-10s\n", "Dish Code", "Dish Name", "Restaurant Name", "Dish Rating", "Price");
    printf("================================================================================\n");

    struct DishNode *curr = head; 
    while(curr != NULL){ 
        printf("%-10d %-25s %-35s %-10.1f %-10.2f\n",   curr->dish.dish_code, curr->dish.dish_name, curr->dish.restaurant_name, curr->dish.rating, curr->dish.price);
        curr = curr->next; 
    }

}


/*
function: erase_dish
description: Erases dish based of dish code. Cannot erase dish in a relationship
*/
int erase_dish(struct DishNode **head, struct DishDriverCode* rel_head){
    int dishCode; 
    int input_status; 

    printf("\tEnter Dish Code: ");
    input_status = scanf("%d", &dishCode);

    if(input_status != 1){
        printf("\tInvalid input. Please try again\n");
        return 0; 
    }

    //Checking if linked list is empty 
    if(*head == NULL){
        printf("\tNo dishes in database\n");
        return 0; 
    }
    //Determining if the dish code has a relationship 
    struct DishDriverCode* rel_finder = rel_head; 
    while(rel_finder != NULL){
        if(relationship_exists(rel_head, dishCode, rel_finder->driver_code)){
            printf("\tRelationship already exists, Dish cannot be deleted.\n");
            return 0; 
        }
        rel_finder = rel_finder->next; 
    }
    //If the head of the linked list is being removed 
    if((*head)->dish.dish_code == dishCode){
        struct DishNode *tmp = *head; 
        *head = (*head)->next; 
        free(tmp); 
        printf("\tDish deleted successfully\n");
        return 1; 
    }
    //Otherwise we need to search for it in the list and connect prev to next 
    struct DishNode *curr = *head;
    struct DishNode *prev = NULL;  
    while(curr != NULL){
        if(curr->dish.dish_code == dishCode){
            prev->next = curr->next; 
            free(curr);
            printf("\tDish deleted succesfully\n");
            return 1; 
        }
        prev = curr; 
        curr = curr->next; 
    }

    printf("\tThe dish code was not found\t");
    return 0; 

}
