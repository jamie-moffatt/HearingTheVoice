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
#import "ILSettingsViewController.h"
#import "ILEndViewController.h"

#import "ILAppManager.h"
#import "ILTimeUtils.h"

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
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Settings" style:UIBarButtonItemStyleBordered target:self action:@selector(settingsButton:)];
    
    [ILAppManager setDefaults];
    
    if ([ILAppManager isFirstRun])
    {
        [ILAppManager setFirstRun:NO];
        [ILAppManager setStartDate:[NSDate date]];
        [ILAppManager setupNotifications];
    }
    
    NSDate *startDate = [ILAppManager getStartDate];
    NSInteger recommendedSession = [ILTimeUtils getSessionByRegistrationDate:startDate];
    _sessionLabel.text = [NSString stringWithFormat:@"%d", recommendedSession];
}

-(void)viewDidAppear:(BOOL)animated
{
    if (![ILAppManager userIsRegistered])
    {
        ILUserFormViewController *userFormView = [[ILUserFormViewController alloc] initWithNibName:@"ILUserForm" bundle:nil];
        [self presentViewController:userFormView animated:YES completion:nil];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)openFirstTimeForm:(id)sender
{
    ILEndViewController *userFormView = [[ILEndViewController alloc] initWithNibName:@"ILEndView" bundle:nil];
    [self presentViewController:userFormView animated:YES completion:nil];
}

- (IBAction)answerQuestions:(id)sender
{
    NSInteger session = [ILTimeUtils getSessionByRegistrationDate:[ILAppManager getStartDate]];
    ILQuestionFormViewController *questionFormView = [[ILQuestionFormViewController alloc] initWithNibName:nil bundle:nil];
    questionFormView.currentSession = session;
    [self.navigationController pushViewController:questionFormView animated:YES];
}

- (IBAction)testNotification:(id)sender
{
    [ILAppManager setupNotifications];
}

- (void)settingsButton:(UIButton *)sender
{
    ILSettingsViewController *settingsView = [[ILSettingsViewController alloc] initWithNibName:nil bundle:nil];
    [self.navigationController pushViewController:settingsView animated:YES];
}

@end
