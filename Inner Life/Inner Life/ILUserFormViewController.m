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
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)submitUserData:(id)sender
{
    [self dismissViewControllerAnimated:true completion:nil];
}
- (IBAction)showUserCodeHelp:(id)sender {
}
- (IBAction)submitUserDetails:(id)sender {
}
@end
