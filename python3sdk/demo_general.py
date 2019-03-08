from client import *
import json


def print_dict(response_json: dict):
    print(json.dumps(response_json, indent=2))


def demo():
    client = GeneralClient()

    # Currency:

    #print_response(client.get_currency_list("NOK"))
    #print_response(client.get_currency_rate("NOK", "USD"))

    # Location:

    latitude = 63.417509
    longitude = 10.405062

    #print_response(client.get_branches_list())
    #print_response(client.get_atm_list())
    #print_response(client.get_closest_branch(longitude, latitude))
    #print_response(client.get_closest_atm(longitude, latitude))
    #print_response(client.get_branch_details(client.get_closest_branch(latitude, longitude).json()[0]["id"]))
    #print_response(client.get_closest_branch_address(""))

    # Test customers in the sandbox:
    demo_customers(client)


def demo_customers(client: GeneralClient):
    all_customers = client.get_test_customers().json()["customers"]
    print("Number of customers: {}".format(len(all_customers)))

    # Show some info for two customers:
    for customer in all_customers:
        ssn = customer["ssn"]
        name = customer["customerName"]

        customer_client = PersonClient(ssn)
        accounts = customer_client.get_accounts().json()["accounts"]

        print("Name: {}".format(name))
        for i, account in enumerate(accounts):
            account_number = account["accountNumber"]
            print(" - Account {}: {}".format(i, account_number))
            print(customer_client.get_transactions(account_number, "2018-06-06", "2018-07-07").content)


if __name__ == "__main__":
    demo()
