//
//  ILDashboardViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILDashboardViewController.h"

#import "ILUserFormViewController.h"
#import "ILQuestionFormViewController.h"

@interface ILDashboardViewController ()

@end

@implementation ILDashboardViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
       
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
     self.title = @"Dashboard";
    
    // TODO: if first time user open the user details form
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)openFirstTimeForm:(id)sender
{
    ILUserFormViewController *userFormView = [[ILUserFormViewController alloc] initWithNibName:@"ILUserForm" bundle:nil];
    [self presentViewController:userFormView animated:YES completion:nil];
}

- (IBAction)answerQuestions:(id)sender
{
    ILQuestionFormViewController *questionFormView = [[ILQuestionFormViewController alloc] initWithNibName:@"ILQuestionForm" bundle:nil];
    [self.navigationController pushViewController:questionFormView animated:YES];
}

@end
