/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * ProductAdvertisingAPI
 *
 * https://webservices.amazon.com/paapi5/documentation/index.html
 */

/*
 * This sample code snippet is for ProductAdvertisingAPI 5.0's GetItems API
 * For more details, refer:
 * https://webservices.amazon.com/paapi5/documentation/get-items.html
 */

import java.util.*;

import com.amazon.paapi5.v1.ApiClient;
import com.amazon.paapi5.v1.ApiException;
import com.amazon.paapi5.v1.ErrorData;
import com.amazon.paapi5.v1.GetItemsRequest;
import com.amazon.paapi5.v1.GetItemsResource;
import com.amazon.paapi5.v1.GetItemsResponse;
import com.amazon.paapi5.v1.Item;
import com.amazon.paapi5.v1.PartnerType;
import com.amazon.paapi5.v1.api.DefaultApi;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Main {

    static void mainCaller() throws FileNotFoundException, IOException {
        main(null) ;
    }


    public static List<String> createListFromFile(String fileUrl) throws FileNotFoundException {
        File file = new File(fileUrl);
        Scanner sc = new Scanner(file);
        List<String> itemIds = new ArrayList<String>();

        while (sc.hasNextLine()) {
            itemIds.add(sc.nextLine());
        }

        return itemIds;
    }

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    // Choose item id(s)
    public static List<String> createList() {
        Scanner input = new Scanner(System.in);
        List<String> itemIds = new ArrayList<String>();
        System.out.println("Enter ASINs:");

        String nextVal = " ";
        while (nextVal.length() > 0) {
            try {
                nextVal = input.nextLine();
                itemIds.add(nextVal);
            } catch (NumberFormatException e) {
                System.out.println("Batch Entry Complete");
            }
        };

        itemIds.remove(itemIds.size() - 1);
        return itemIds;
    }

    public static GetItemsRequest formRequest(List<String> itemIds, String partnerTag, List<GetItemsResource> getItemsResources) {
        GetItemsRequest getItemsRequest = new GetItemsRequest().itemIds(itemIds).partnerTag(partnerTag)
                .resources(getItemsResources).partnerType(PartnerType.ASSOCIATES);
        return getItemsRequest;
    }

    /**
     * Returns Item mapped to ASIN
     *
     * @param items Items
     * @return Items mapped to ASIN
     */
    private static Map<String, Item> parse_response(List<Item> items) {
        Map<String, Item> mappedResponse = new HashMap<String, Item>();
        for (Item item : items) {
            mappedResponse.put(item.getASIN(), item);
        }
        return mappedResponse;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        ApiClient client = new ApiClient();

        // Add your credentials
        // Please add your access key here
        client.setAccessKey("[KEY]");
        // Please add your secret key here
        client.setSecretKey("[KEY]");

        // Enter your partner tag (store/tracking id)
        String partnerTag = "[ID]";

        /*
         * PAAPI Host and Region to which you want to send request. For more
         * details refer:
         * https://webservices.amazon.com/paapi5/documentation/common-request-parameters.html#host-and-region
         */
        client.setHost("webservices.amazon.com");
        client.setRegion("us-east-1");

        DefaultApi api = new DefaultApi(client);

        // Request initialization
        /*
         * Choose resources you want from GetItemsResource enum For more
         * details, refer:
         * https://webservices.amazon.com/paapi5/documentation/get-items.html#resources-parameter
         */
        List<GetItemsResource> getItemsResources = new ArrayList<GetItemsResource>();
        getItemsResources.add(GetItemsResource.ITEMINFO_TITLE);
        getItemsResources.add(GetItemsResource.OFFERS_LISTINGS_PRICE);

        //String fileUrl = "C:\\Users\\George Bush\\IdeaProjects\\AmazonSearch\\out\\artifacts\\AmazonSearch_jar\\ASIN.txt";
        //String fileUrlOut = "C:\\Users\\George Bush\\IdeaProjects\\AmazonSearch\\out\\artifacts\\AmazonSearch_jar\\output.txt";

        String fileUrl = "C:\\Users\\Administrator\\Desktop\\Amazon Search\\ASIN.txt";
        String fileUrlOut = "C:\\Users\\Administrator\\Desktop\\Amazon Search\\output.txt";

        List<String> itemIds = createListFromFile(fileUrl);

        try {
            File myObj = new File(fileUrlOut);
            if (myObj.createNewFile()) {
            }
            else {
            }
        } catch (IOException e) {

        }


        FileWriter myWriter = new FileWriter(fileUrlOut);



        // Method to input ASINs from Terminal
        /*
        itemIds = createList();
        */


        do {
            List<String> itemIdsClipped = new ArrayList<String>();
            for (int i = itemIds.size() - 1; i >= 0 && itemIdsClipped.size() < 10; i--) {
                itemIdsClipped.add(itemIds.get(i));
                itemIds.remove(i);
            }

            // Forming the request
            GetItemsRequest getItemsRequest = formRequest(itemIdsClipped, partnerTag, getItemsResources);

            wait(1000);

            try {
                // Sending the request
                GetItemsResponse response = api.getItems(getItemsRequest);

                // Parsing the request
                if (response.getItemsResult() != null) {
                    Map<String, Item> responseList = parse_response(response.getItemsResult().getItems());
                    for (String itemId : itemIdsClipped) {
                        if (response.getItemsResult().getItems() != null) {
                            if (responseList.get(itemId) != null) {
                                Item item = responseList.get(itemId);
                                if (item.getASIN() != null) {
                                    myWriter.write(item.getASIN() + "\t");
                                }
                                if (item.getDetailPageURL() != null) {
                                    myWriter.write(item.getDetailPageURL() + "\t");
                                }
                                if (item.getItemInfo() != null && item.getItemInfo().getTitle() != null
                                        && item.getItemInfo().getTitle().getDisplayValue() != null) {
                                    myWriter.write(item.getItemInfo().getTitle().getDisplayValue() + "\t");
                                }
                                if (item.getOffers() != null && item.getOffers().getListings() != null
                                        && item.getOffers().getListings().get(0).getPrice() != null
                                        && item.getOffers().getListings().get(0).getPrice().getDisplayAmount() != null) {
                                    myWriter.write(item.getOffers().getListings().get(0).getPrice().getDisplayAmount() + "\r\n");
                                }
                                if (!(item.getOffers() != null && item.getOffers().getListings() != null
                                        && item.getOffers().getListings().get(0).getPrice() != null
                                        && item.getOffers().getListings().get(0).getPrice().getDisplayAmount() != null)) {
                                    myWriter.write("\r\n");
                                }

                            } else {
                                System.out.println("Item not found, check errors");
                            }
                        }
                    }
                }
                if (response.getErrors() != null) {
                    System.out.println("Printing errors:\nPrinting Errors from list of Errors");
                    for (ErrorData error : response.getErrors()) {
                        System.out.println("Error code: " + error.getCode());
                        System.out.println("Error message: " + error.getMessage());
                    }
                }
            } catch (ApiException exception) {
                // Exception handling
                System.out.println("Error calling PA-API 5.0!");
                System.out.println("Status code: " + exception.getCode());
                System.out.println("Errors: " + exception.getResponseBody());
                System.out.println("Message: " + exception.getMessage());
                if (exception.getResponseHeaders() != null) {
                    // Printing request reference
                    System.out.println("Request ID: " + exception.getResponseHeaders().get("x-amzn-RequestId"));
                }
                // exception.printStackTrace();
            } catch (Exception exception) {
                System.out.println("Exception message: " + exception.getMessage());
                // exception.printStackTrace();
            }
            itemIdsClipped.clear();
        } while (itemIds.size() > 1);

        myWriter.close();
        System.exit(0);
        mainCaller();

    }

}
