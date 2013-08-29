//
//  ILEndViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 15/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILEndViewController.h"
#import "ILAppManager.h"
#import "common.h"
#import "TestFlight.h"

@interface ILEndViewController ()

@end

@implementation ILEndViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	[TestFlight passCheckpoint:@"Reached EndViewController"];
    _lblSamplesComplete.text = [NSString stringWithFormat:@"You completed %d/28 samples.", [[ILAppManager getSessionsCompleted] count]];
    _lblAverageResponseTime.text = [ILAppManager getFormattedAverageResponseTime];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)yesButton:(UIButton *)sender
{
    [self submitRequest: YES];
}

- (IBAction)noButton:(UIButton *)sender
{
    [self submitRequest: NO];
}

- (void)submitRequest:(BOOL)allowDataUse
{
    [ILAppManager setPermissionToStudyData:allowDataUse];
    [ILAppManager setDataPermissionsValid:YES];
    
    UIAlertView *yesAlert =
    [[UIAlertView alloc]
     initWithTitle:@"Submitting Your Request"
     message:@"Notifying the database with new permissions."
     delegate:nil
     cancelButtonTitle:@"OK"
     otherButtonTitles:nil];
    
    UIAlertView *noAlert =
    [[UIAlertView alloc]
     initWithTitle:@"Submitting Your Request"
     message:@"Removing your data from the study."
     delegate:nil
     cancelButtonTitle:@"OK"
     otherButtonTitles:nil];
    
    if (allowDataUse)
    {
        [yesAlert show];
    }
    else
    {
        [noAlert show];
    }
    
    NSURL *permissionsAPI_URL = [NSURL URLWithString:PERMISSIONS_API_ENDPOINT];
    NSMutableURLRequest *URL_Request = [NSMutableURLRequest requestWithURL:permissionsAPI_URL];
    [URL_Request setHTTPMethod:@"POST"];
    [URL_Request setValue:@"text/xml" forHTTPHeaderField:@"Content-Type"];
    NSString* permissionXML = [NSString stringWithFormat:@"<user id=\"%d\" useDataPermission=\"%@\" />", [ILAppManager getUser].userID, allowDataUse ? @"true" : @"false"];
    NSLog(@"%@", permissionXML);
    [URL_Request setHTTPBody:[permissionXML dataUsingEncoding:NSUTF8StringEncoding]];
    
    [NSURLConnection sendAsynchronousRequest:URL_Request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
     {
         [yesAlert dismissWithClickedButtonIndex:0 animated:NO];
         [noAlert dismissWithClickedButtonIndex:0 animated:NO];
         
         if (data)
         {
             NSString *s = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
             
             NSLog(@"%@",s);
             
             [ILAppManager setPermissionsHaveSynchronized:YES];
             UIAlertView *alert =
             [[UIAlertView alloc]
              initWithTitle:@"Request Successful"
              message:@"Thanks. You can now uninstall Inner Life. If you have any questions about the app, you can contant Dr. Ben Alderson-Day at benjamin.alderson-day@durham.ac.uk."
              delegate:nil
              cancelButtonTitle:@"OK"
              otherButtonTitles:nil];
             [alert show];
         }
         else
         {
             [ILAppManager setPermissionsHaveSynchronized:NO];
             UIAlertView *alert =
             [[UIAlertView alloc]
              initWithTitle:@"Request Failed"
              message:@"Unfortunately, your data requestion did not make it to the database. Ensure that you have an internet connection and restart the application."
              delegate:nil
              cancelButtonTitle:@"OK"
              otherButtonTitles:nil];
             [alert show];
         }
     }];

}

@end
