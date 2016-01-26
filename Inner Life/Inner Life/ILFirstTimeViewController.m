//
//  ILFirstTimeViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 22/01/2016.
//  Copyright © 2016 Matthew Bates. All rights reserved.
//

#import "ILFirstTimeViewController.h"

#import "common.h"
#import "ILAppManager.h"

@interface ILFirstTimeViewController ()

@end

@implementation ILFirstTimeViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (!string.length)
    {
        return YES;
    }
    
    // Prevent invalid character input, if keyboard is numberpad
    if (textField.keyboardType == UIKeyboardTypeNumberPad)
    {
        if ([string rangeOfCharacterFromSet:[[NSCharacterSet decimalDigitCharacterSet] invertedSet]].location != NSNotFound)
        {
            return NO;
        }
    }
    
    // verify max length has not been exceeded
    NSString *updatedText = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    if (updatedText.length > 4)
    {
        return NO;
    }
    
    return YES;
}


- (IBAction)onStartDemoTapped:(UIButton *)sender
{
    
}

- (IBAction)onStartFullApp:(UIButton *)sender
{
    NSString* studyCodeText = _txtStudyCode.text;
    
    if (studyCodeText.length > 0 && studyCodeText.length < 4)
    {
        UIAlertController* incorrectFormatAlert = [UIAlertController
                                                   alertControllerWithTitle:@"Format Error"
                                                   message:@"Study code should be 4-digits or left blank."
                                                   preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* okButton = [UIAlertAction
                                   actionWithTitle:@"OK"
                                   style:UIAlertActionStyleDefault
                                   handler:^(UIAlertAction * action)
                                   {
                                   }];
        
        [incorrectFormatAlert addAction:okButton];
        [self presentViewController:incorrectFormatAlert animated:YES completion:nil];
        
        return;
    }
    
    [ILAppManager setStudyCode:studyCodeText.length == 4 ? studyCodeText : DEFAULT_STUDY_CODE];
}
@end
