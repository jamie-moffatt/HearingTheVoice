//
//  ILUserFormViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 14/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILUserFormViewController.h"

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
    
    NSLog(@"%@", userXML);
    
    // TODO: Send user XML to the backend (check for acknowledgement and save new userID)
    
    [self dismissViewControllerAnimated:true completion:nil];
}
@end
