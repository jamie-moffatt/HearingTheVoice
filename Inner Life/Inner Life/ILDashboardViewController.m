//
//  ILDashboardViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILDashboardViewController.h"

#import "ILUserFormViewController.h"

@interface ILDashboardViewController ()

@end

@implementation ILDashboardViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        self.title = @"Inner Life";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)openFirstTimeForm:(id)sender
{
    NSLog(@"Button Press");
    ILUserFormViewController *userFormView = [[ILUserFormViewController alloc] initWithNibName:@"ILUserForm" bundle:nil];
    [self presentViewController:userFormView animated:YES completion:nil];
}

@end
