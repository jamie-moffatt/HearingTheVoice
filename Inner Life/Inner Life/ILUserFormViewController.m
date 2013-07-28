//
//  ILUserFormViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 14/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILUserFormViewController.h"
#import "common.h"
#import "ILUser.h"
#import "ILAppManager.h"

@interface ILUserFormViewController ()

@end

@implementation ILUserFormViewController

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
    
    // TODO: Load data from the device if it already exists
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.tag == 1)
    {
        [textField resignFirstResponder];
        [[self.view viewWithTag:2] becomeFirstResponder];
        return YES;
    }
    else
    {
        [textField resignFirstResponder];
        return YES;
    }
}

- (IBAction)showUserCodeHelp:(id)sender
{
    UIAlertView *userHelpDialog = [[UIAlertView alloc] initWithTitle:@"Anonymous User Code" message:@"Your anonymous code is made up of the first two letters of your surname, followed by the first two letters of the place you were born, completed by the day you were born.\n\nFor example, Helen Smith, who was born in Liverpool on the 17th September would have the anonymous code: SMLI17" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
    [userHelpDialog show];
}

- (IBAction)submitUserDetails:(id)sender
{
    NSString *userCode = _txtUserCode.text;
    NSString *userAge  = _txtAge.text;
    NSString *userGender = [_segGender titleForSegmentAtIndex:_segGender.selectedSegmentIndex];
    userGender = [userGender uppercaseString];
    // TODO: Validate user input
    // TODO: Save user to device
    
    NSString *userXML = [NSString stringWithFormat:@"<user code=\"%@\" age=\"%@\" gender=\"%@\" />", userCode, userAge, userGender];
    
    NSLog(@"Submitting UserXML: %@", userXML);
    
    // TODO: Send user XML to the backend (check for acknowledgement and save new userID)
    NSURL *userAPI_URL = [NSURL URLWithString:USER_API_ENDPOINT];
    NSMutableURLRequest *URL_Request = [NSMutableURLRequest requestWithURL:userAPI_URL];
    [URL_Request setHTTPMethod:@"POST"];
    [URL_Request setValue:@"text/xml" forHTTPHeaderField:@"Content-Type"];
    [URL_Request setHTTPBody:[userXML dataUsingEncoding:NSUTF8StringEncoding]];
    
    [NSURLConnection sendAsynchronousRequest:URL_Request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
     {
         if (data)
         {
             NSString *dataStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
             NSInteger userID = [dataStr integerValue];
             
             NSLog(@"New User Accepted with ID: %d", userID);
             
             ILUser *newUser = [[ILUser alloc] init];
             newUser.userID = userID;
             newUser.userCode = userCode;
             newUser.age = [userAge integerValue];
             newUser.gender = userGender;
             
             [ILAppManager setUser:newUser];
             NSLog(@"Set New User: %@", newUser);
             
             [self dismissViewControllerAnimated:true completion:nil];
         }
     }];
}
@end
