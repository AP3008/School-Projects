
#ifndef DRIVER_H
#define DRIVER_H

#include "snackaroo.h"

//Driver Functions

int insert_driver(struct DriverNode** head);

void search_driver(struct DriverNode* head);

int update_driver(struct DriverNode* head);

void print_all_drivers(struct DriverNode* head); 

int erase_driver(struct DriverNode** head, struct DishDriverCode* rel_head);

//Utility Functions

int driver_code_exists(int code, struct DriverNode* head); 

int is_valid_licence_plate(const char* plate); 

const char* get_colour_name(int colour);  // ADD THIS LINE

#endif
