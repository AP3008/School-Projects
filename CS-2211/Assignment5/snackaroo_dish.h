
#ifndef DISH_H
#define DISH_H

#include "snackaroo.h"

// Declaring DashDriverCode
struct DishDriverCode;

// Utility function
void clearInputBuffer(); 

// Dish functions
int insert_dish(struct DishNode** head);

void search_dish(struct DishNode* head);

int update_dish(struct DishNode* head);

void print_all_dishes(struct DishNode* head); 

int erase_dish(struct DishNode** head, struct DishDriverCode* rel_head); 

int dish_code_exists(int code, struct DishNode* node);

#endif
