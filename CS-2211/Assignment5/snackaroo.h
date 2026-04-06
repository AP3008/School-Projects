
#ifndef SNACKAROO_H
#define SNACKAROO_H

/*Macros Section*/

//Dish related macros

#define MAX_DISH_NAME_LENGTH 100
#define MAX_RESTAURANT_NAME_LENGTH 100
#define MIN_RATING 0.0
#define MAX_RATING 10.0
#define MIN_PRICE 0.0

//Driver related macros

#define MAX_DRIVER_NAME_LENGTH 50 
#define MIN_LICENCE_LENGTH 2
#define MAX_LICENCE_LENGTH 9

//Vehicle colours

#define COLOUR_RED 0
#define COLOUR_GREEN 1
#define COLOUR_BLUE 2
#define COLOUR_GREY 3
#define COLOUR_WHITE 4
#define COLOUR_BLACK 5
#define COLOUR_OTHER 6
#define MIN_COLOUR 0
#define MAX_COLOUR 6

//Structure Definitions

//Dish structure
struct Dish{
    int dish_code;
    char dish_name[MAX_DISH_NAME_LENGTH];
    char restaurant_name[MAX_RESTAURANT_NAME_LENGTH];
    float rating; 
    float price; 
};

//Driver structure
struct Driver{
    int driver_code;
    char driver_name[MAX_DRIVER_NAME_LENGTH];
    int vehicle_colour; 
    char licence_plate[MAX_LICENCE_LENGTH];
};

//Dish node structure
struct DishNode{
    struct Dish dish; 
    struct DishNode *next; 
};

//Driver Node structure 
struct DriverNode{
    struct Driver driver;
    struct DriverNode *next; 
};

//Dish/Driver relationship structure
struct DishDriverCode{
    int dish_code;
    int driver_code; 
    struct DishDriverCode *next;
};

#endif
